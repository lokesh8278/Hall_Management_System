package com.hallbooking.hall_service.serviceImpl;

import com.hallbooking.bookingService.entity.Bookings;
import com.hallbooking.bookingService.repository.BookingRepo;
import com.hallbooking.enums.AmenityType;
import com.hallbooking.hall_service.dto.request.HallRequest;
import com.hallbooking.hall_service.dto.request.RoomRequest;
import com.hallbooking.hall_service.dto.response.*;
import com.hallbooking.hall_service.entity.*;
import com.hallbooking.hall_service.interfaces.HallService;
import com.hallbooking.hall_service.repository.*;
import com.hallbooking.userservice.exceptions.ResourceNotFoundException;
import com.hallbooking.utilis.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HallServiceImpl implements HallService {

    private final HallRepository hallRepository;
    private final RoomRepository roomRepository;
    private final FileValidator fileValidator;
    private final AmenityRepository amenityRepository;
    private final BookingRepo bookingRepository;
    private final HallDocumentRepository hallDocumentRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Value("${file.upload-dir}")
    private String uploadDirPath;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private Hall getHallOrThrow(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found with id: " + id));
    }

    private List<String> mapAmenities(List<Amenity> amenities) {
        if (amenities == null) return List.of();
        return amenities.stream().map(a -> a.getType().name()).toList();
    }

    @Override
    public HallResponse createHall(HallRequest request, Long vendorId) {
        Point point = geometryFactory.createPoint(new Coordinate(request.getLng(), request.getLat()));

        Hall hall = Hall.builder()
                .name(request.getName())
                .location(point)
                .ownerId(vendorId)
                .baseprice(request.getBasePrice())
                .virtualTourUrl(request.getVirtualTourUrl())
                .isActive(true)
                .build();

        Hall saved = hallRepository.save(hall);

        return HallResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .ownerId(saved.getOwnerId())
                .latitude(saved.getLatitude())
                .longitude(saved.getLongitude())
                .basePrice(saved.getBaseprice())
                .virtualTourUrl(saved.getVirtualTourUrl())
                .amenities(List.of())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HallResponse> getAllHalls() {
        var halls = hallRepository.findAllActiveHalls(org.springframework.data.domain.Pageable.unpaged()).getContent();

        return halls.stream().map(hall -> {
            // touch collections inside TX; SUBSELECT will fetch safely in separate queries
            var rooms = hall.getRooms();
            var amenities = hall.getAmenities();

            String thumbnailUrl = hallDocumentRepository.findThumbnailIdsByHallId(hall.getId()).stream()
                    .findFirst()
                    .map(id -> baseUrl + "/api/halls/documents/" + id + "/image")
                    .orElse(null);

            int guestCapacity = (rooms == null) ? 0 : rooms.stream().mapToInt(Room::getCapacity).sum();

            return HallResponse.builder()
                    .id(hall.getId())
                    .name(hall.getName())
                    .ownerId(hall.getOwnerId())
                    .virtualTourUrl(hall.getVirtualTourUrl())
                    .thumbnailUrl(thumbnailUrl)
                    .basePrice(hall.getBaseprice())
                    .amenities(mapAmenities(amenities))
                    .guestCapacity(guestCapacity)
                    .rating(4.3)
                    .contactNumber("+91-9876543210")
                    .latitude(hall.getLatitude())
                    .longitude(hall.getLongitude())
                    .build();
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HallResponse getHall(Long id) {
        Hall hall = getHallOrThrow(id);

        // Trigger safe collection load if needed
        if (hall.getAmenities() != null) hall.getAmenities().size();

        List<HallDocumentResponse> docs =
                hallDocumentRepository.fetchDocumentMeta(id, baseUrl + "/api/halls/documents/");

        return HallResponse.builder()
                .id(hall.getId())
                .name(hall.getName())
                .ownerId(hall.getOwnerId())
                .virtualTourUrl(hall.getVirtualTourUrl())
                .basePrice(hall.getBaseprice())
                .amenities(mapAmenities(hall.getAmenities()))
                .documents(docs)
                .build();
    }

    @Override
    public String deleteHall(Long id, Long vendorId) {
        Hall hall = getHallOrThrow(id);
        if (!hall.getOwnerId().equals(vendorId)) throw new AccessDeniedException("Unauthorized");
        hall.setActive(false);
        hallRepository.save(hall);
        return "Hall marked as deleted successfully";
    }

    @Override
    public HallResponse updateHall(Long id, HallRequest request, Long vendorId) {
        Hall hall = getHallOrThrow(id);
        if (!hall.getOwnerId().equals(vendorId)) throw new AccessDeniedException("Unauthorized");

        if (request.getName() != null) hall.setName(request.getName());
        if (request.getLat() != 0 && request.getLng() != 0)
            hall.setLocation(geometryFactory.createPoint(new Coordinate(request.getLng(), request.getLat())));
        if (request.getVirtualTourUrl() != null) hall.setVirtualTourUrl(request.getVirtualTourUrl());
        if (request.getBasePrice() > 0) hall.setBaseprice(request.getBasePrice());
        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            List<Amenity> amenities = request.getAmenities().stream()
                    .map(a -> amenityRepository.findByType(AmenityType.valueOf(a))
                            .orElseThrow(() -> new RuntimeException("Amenity not found: " + a)))
                    .toList();
            hall.setAmenities(amenities);
        }

        Hall updated = hallRepository.save(hall);
        log.info("🔧 Updating Hall ID: {}", id);
        log.info("Vendor ID from token: {}", vendorId);
        log.info("Incoming HallRequest: {}", request);

        return HallResponse.builder()
                .id(updated.getId())
                .name(updated.getName())
                .ownerId(updated.getOwnerId())
                .virtualTourUrl(updated.getVirtualTourUrl())
                .basePrice(updated.getBaseprice())
                .amenities(mapAmenities(updated.getAmenities()))
                .build();
    }

    @Override
    public RoomResponse addRoom(Long hallId, RoomRequest request, Long vendorId) {
        Hall hall = getHallOrThrow(hallId);
        if (!hall.getOwnerId().equals(vendorId)) throw new AccessDeniedException("Unauthorized");
        Room room = Room.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .price(request.getPrice())
                .hall(hall)
                .build();
        room = roomRepository.save(room);

        return new RoomResponse(room.getId(), room.getName(), room.getCapacity(), room.getPrice());
    }

    @Override
    public RoomResponse updateRoom(Long id, RoomRequest request, Long vendorId) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!room.getHall().getOwnerId().equals(vendorId)) throw new AccessDeniedException("Unauthorized");
        if (request.getName() != null) room.setName(request.getName());
        if (request.getCapacity() != null) room.setCapacity(request.getCapacity());
        if (request.getPrice() != null) room.setPrice(request.getPrice());
        room = roomRepository.save(room);
        return new RoomResponse(room.getId(), room.getName(), room.getCapacity(), room.getPrice());
    }

    @Override
    public String deleteRoom(Long id, Long vendorId) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!room.getHall().getOwnerId().equals(vendorId)) throw new AccessDeniedException("Unauthorized");
        roomRepository.delete(room);
        return "Room deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByHall(Long id) {
        Hall hall = hallRepository.findByIdWithRooms(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found with id: " + id));
        return hall.getRooms().stream()
                .map(r -> new RoomResponse(r.getId(), r.getName(), r.getCapacity(), r.getPrice()))
                .toList();
    }

    @Override
    public List<HallResponse> searchHalls(double lat, double lng, double radius) {
        return hallRepository.findHallsNearby(lat, lng, radius).stream()
                .map(h -> HallResponse.builder()
                        .id(h.getId())
                        .name(h.getName())
                        .ownerId(h.getOwnerId())
                        .amenities(mapAmenities(h.getAmenities()))
                        .virtualTourUrl(h.getVirtualTourUrl())
                        .build())
                .toList();
    }

    @Override
    public AvailabilityResponse isAvailable(Long hallId, String dateStr) {
        Date checkin = Date.valueOf(dateStr);
        Date checkout = new Date(checkin.getTime() + 86400000);

        List<Room> rooms = roomRepository.findByHall_Id(hallId);
        List<Bookings> bookings = bookingRepository.findByHallIdWithDetails(hallId);

        Set<Long> bookedRoomIds = new HashSet<>();

        for (Room room : rooms) {
            boolean isBooked = bookings.stream()
                    .filter(b -> b.getRoom() != null && b.getRoom().getId().equals(room.getId()))
                    .filter(b -> List.of("PENDING", "CONFIRMED").contains(b.getStatus()))
                    .anyMatch(b -> checkin.before(b.getCheckout()) && checkout.after(b.getCheckin()));

            if (isBooked) bookedRoomIds.add(room.getId());
        }

        List<Long> availableRoomIds = rooms.stream()
                .filter(r -> !bookedRoomIds.contains(r.getId()))
                .map(Room::getId)
                .toList();

        logAvailability(hallId, checkin, checkout, bookings, bookedRoomIds, availableRoomIds);

        log.info("🛏 All room IDs: {}", rooms.stream().map(Room::getId).toList());
        log.info("📅 Checkin={}, Checkout={}", checkin, checkout);
        bookings.forEach(b -> log.info("🔁 Room={}, In={}, Out={}, Status={}",
                b.getRoom() != null ? b.getRoom().getId() : "null", b.getCheckin(), b.getCheckout(), b.getStatus()));
        log.info("❌ Booked: {}", bookedRoomIds);
        log.info("✅ Available: {}", availableRoomIds);

        return AvailabilityResponse.builder()
                .available(!availableRoomIds.isEmpty())
                .message(!availableRoomIds.isEmpty() ? "Available" : "No rooms available")
                .availableRooms(availableRoomIds)
                .build();
    }

    @Async
    public void logAvailability(Long hallId, Date checkin, Date checkout, List<Bookings> bookings, Set<Long> bookedIds, List<Long> availableIds) {
        log.info("🛏 All room IDs: {}", bookings.stream().map(Bookings::getRoom).map(Room::getId).toList());
        log.info("📅 Checkin={}, Checkout={}", checkin, checkout);
        bookings.forEach(b -> log.info("🔁 Room={}, In={}, Out={}, Status={}",
                b.getRoom() != null ? b.getRoom().getId() : "null", b.getCheckin(), b.getCheckout(), b.getStatus()));
        log.info("❌ Booked: {}", bookedIds);
        log.info("✅ Available: {}", availableIds);
    }

    @Override
    public AvailableDatesResponse getAvailableDates(Long id, int days) {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(days);

        Set<LocalDate> booked = bookingRepository.findByHallIdWithDetails(id).stream()
                .flatMap(b -> {
                    LocalDate checkin = b.getCheckin().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate checkout = b.getCheckout().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return checkin.datesUntil(checkout.plusDays(1));
                })
                .filter(d -> !d.isBefore(today) && !d.isAfter(end))
                .collect(Collectors.toSet());

        List<LocalDate> available = today.datesUntil(end.plusDays(1))
                .filter(d -> !booked.contains(d))
                .collect(Collectors.toList());

        return new AvailableDatesResponse(id, available);
    }

    @Override
    public String uploadImage(Long id, MultipartFile image, String desc, Long vendorId) throws IOException {
        Hall hall = getHallOrThrow(id);
        if (!hall.getOwnerId().equals(vendorId)) throw new AccessDeniedException("Unauthorized");
        if (!image.getContentType().startsWith("image/")) throw new IllegalArgumentException("Invalid image file");

        HallDocument doc = HallDocument.builder()
                .fileName(Paths.get(image.getOriginalFilename()).getFileName().toString())
                .description(desc)
                .fileData(image.getBytes())
                .hall(hall).build();
        hall.getDocuments().add(doc);
        hallRepository.save(hall);
        return "Uploaded: " + doc.getFileName();
    }

    @Override
    public double getPricing(Long id, String date) {
        Hall hall = getHallOrThrow(id);
        double price = hall.getBaseprice();
        if (date.endsWith("-12-25")) price += 500;
        return price;
    }

    @Override
    public List<String> getAvailableAmenities() {
        return Arrays.stream(AmenityType.values()).map(Enum::name).toList();
    }

    @Override
    public String uploadVirtualTour(Long id, MultipartFile file, String url, Long vendorId) throws IOException {
        Hall hall = getHallOrThrow(id);
        if (!hall.getOwnerId().equals(vendorId)) throw new AccessDeniedException("Unauthorized");

        if (url != null && !url.isBlank()) {
            hall.setVirtualTourUrl(url);
            hallRepository.save(hall);
            return "Link saved: " + url;
        }

        if (!fileValidator.isValidFileType(file)) throw new IllegalArgumentException("Invalid file");

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dir = new File(uploadDirPath);
        if (!dir.exists()) dir.mkdirs();
        file.transferTo(new File(uploadDirPath + File.separator + fileName));

        hall.setVirtualTourUrl("/uploads/" + fileName);
        hallRepository.save(hall);
        return "Uploaded: " + hall.getVirtualTourUrl();
    }

    @Override
    public byte[] getDocumentImage(Long id) {
        return hallDocumentRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found")).getFileData();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HallResponse> getHallsByVendor(Long vendorId) {
        var halls = hallRepository.findByOwnerIdAndActive(vendorId);

        return halls.stream().map(hall -> HallResponse.builder()
                .id(hall.getId())
                .name(hall.getName())
                .ownerId(hall.getOwnerId())
                .virtualTourUrl(hall.getVirtualTourUrl())
                .thumbnailUrl(hallDocumentRepository.findThumbnailIdsByHallId(hall.getId()).stream()
                        .findFirst()
                        .map(id -> baseUrl + "/api/halls/documents/" + id + "/image").orElse(null))
                .basePrice(hall.getBaseprice())
                .amenities(mapAmenities(hall.getAmenities()))
                .guestCapacity(hall.getRooms() != null ? hall.getRooms().stream().mapToInt(Room::getCapacity).sum() : 0)
                .rating(4.3)
                .contactNumber("+91-9876543210")
                .latitude(hall.getLatitude())
                .longitude(hall.getLongitude())
                .build()).toList();
    }
}
