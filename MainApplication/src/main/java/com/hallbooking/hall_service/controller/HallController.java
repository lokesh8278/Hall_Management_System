package com.hallbooking.hall_service.controller;

import com.hallbooking.hall_service.dto.request.AvailabilityRequest;
import com.hallbooking.hall_service.dto.request.HallRequest;
import com.hallbooking.hall_service.dto.request.RoomRequest;
import com.hallbooking.hall_service.dto.response.*;
import com.hallbooking.hall_service.interfaces.HallService;
import com.hallbooking.hall_service.repository.HallDocumentRepository;
import com.hallbooking.utilis.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/halls")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class HallController {

    private final HallService hallService;
    private final HallDocumentRepository hallDocumentRepository;
    private final JWTUtil jwtUtil;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private String cleanToken(String authHeader) {
        return authHeader.startsWith("Bearer ") ? authHeader.substring(7).trim() : authHeader;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('HALL_VENDOR')")
    public ResponseEntity<HallResponse> createHall(@RequestBody HallRequest request,
                                                   @RequestHeader("Authorization") String authHeader) {
        Long vendorId = jwtUtil.extractUserId(cleanToken(authHeader));
        HallResponse response = hallService.createHall(request, vendorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/gethalls")
    public List<HallResponse> getAllHalls() {
        return hallService.getAllHalls();
    }

    @GetMapping("/{id}")
    public HallResponse getHall(@PathVariable Long id) {
        return hallService.getHall(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HALL_VENDOR')")
    public HallResponse updateHall(@PathVariable Long id,
                                   @RequestBody HallRequest request,
                                   @RequestHeader("Authorization") String authHeader) {
        Long vendorId = jwtUtil.extractUserId(cleanToken(authHeader));
        return hallService.updateHall(id, request, vendorId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HALL_VENDOR')")
    public String deleteHall(@PathVariable Long id,
                             @RequestHeader("Authorization") String authHeader) {
        Long vendorId = jwtUtil.extractUserId(cleanToken(authHeader));
        return hallService.deleteHall(id, vendorId);
    }

    @PostMapping("/{id}/rooms")
    @PreAuthorize("hasRole('HALL_VENDOR')")
    public RoomResponse addRoom(@PathVariable Long id,
                                @RequestBody RoomRequest request,
                                @RequestHeader("Authorization") String authHeader) {
        Long vendorId = jwtUtil.extractUserId(cleanToken(authHeader));
        return hallService.addRoom(id, request, vendorId);
    }

    @PutMapping("/rooms/{id}")
    @PreAuthorize("hasRole('HALL_VENDOR')")
    public RoomResponse updateRoom(@PathVariable Long id,
                                   @RequestBody RoomRequest request,
                                   @RequestHeader("Authorization") String authHeader) {
        Long vendorId = jwtUtil.extractUserId(cleanToken(authHeader));
        return hallService.updateRoom(id, request, vendorId);
    }

    @DeleteMapping("/rooms/{id}")
    @PreAuthorize("hasRole('HALL_VENDOR')")
    public String deleteRoom(@PathVariable Long id,
                             @RequestHeader("Authorization") String authHeader) {
        Long vendorId = jwtUtil.extractUserId(cleanToken(authHeader));
        return hallService.deleteRoom(id, vendorId);
    }

    @GetMapping("/{id}/rooms")
    public List<RoomResponse> getRoomsForHall(@PathVariable Long id) {
        return hallService.getRoomsByHall(id);
    }

    @GetMapping("/search")
    public List<HallResponse> searchHalls(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
        return hallService.searchHalls(lat, lng, radius);
    }

    @PostMapping("/{id}/availability")
    public AvailabilityResponse checkAvailability(@PathVariable Long id, @RequestBody AvailabilityRequest request) {
        return hallService.isAvailable(id, request.getDate());
    }

    @GetMapping("/{id}/available-dates")
    public AvailableDatesResponse getAvailableDates(@PathVariable Long id, @RequestParam int days) {
        return hallService.getAvailableDates(id, days);
    }

    @GetMapping("/{id}/pricing")
    public ResponseEntity<Double> getPricing(@PathVariable Long id, @RequestParam String date) {
        return ResponseEntity.ok(hallService.getPricing(id, date));
    }

    @GetMapping("/filters")
    public List<String> getAvailableAmenities() {
        return hallService.getAvailableAmenities();
    }

    @PostMapping("/{id}/upload-image")
    @PreAuthorize("hasRole('HALL_VENDOR')")
    public ResponseEntity<String> uploadImage(@PathVariable Long id,
                                              @RequestParam("image") MultipartFile image,
                                              @RequestParam("description") String description,
                                              @RequestHeader("Authorization") String authHeader) throws Exception {
        Long vendorId = jwtUtil.extractUserId(cleanToken(authHeader));
        String result = hallService.uploadImage(id, image, description, vendorId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/upload-virtual-tour")
    @PreAuthorize("hasRole('HALL_VENDOR')")
    public String uploadVirtualTour(@PathVariable Long id,
                                    @RequestParam MultipartFile file,
                                    @RequestParam(required = false) String externalUrl,
                                    @RequestHeader("Authorization") String authHeader) throws Exception {
        Long vendorId = jwtUtil.extractUserId(cleanToken(authHeader));
        return hallService.uploadVirtualTour(id, file, externalUrl, vendorId);
    }

    @GetMapping("/documents/{id}/image")
    public ResponseEntity<byte[]> getHallDocumentImage(@PathVariable Long id) {
        byte[] imageBytes = hallService.getDocumentImage(id);
        return ResponseEntity.ok().header("Content-Type", "image/jpeg").body(imageBytes);
    }

    @GetMapping("/documents/{hallId}/meta")
    public ResponseEntity<List<HallDocumentResponse>> getDocumentMetadata(@PathVariable Long hallId) {
        List<HallDocumentResponse> docs = hallDocumentRepository.fetchDocumentMeta(hallId, baseUrl + "/api/halls/documents/");
        return ResponseEntity.ok(docs);
    }

    @GetMapping("/vendor")
    public List<HallResponse> getVendorHalls(@RequestHeader("Authorization") String authHeader) {
        String token = cleanToken(authHeader);
        if (!jwtUtil.isVendor(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Only vendors allowed");
        }
        Long vendorId = jwtUtil.extractUserId(token);
        return hallService.getHallsByVendor(vendorId);
    }
}
