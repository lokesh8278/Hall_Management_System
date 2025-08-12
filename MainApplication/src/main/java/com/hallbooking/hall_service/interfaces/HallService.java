package com.hallbooking.hall_service.interfaces;

import com.hallbooking.hall_service.dto.request.HallRequest;
import com.hallbooking.hall_service.dto.request.RoomRequest;
import com.hallbooking.hall_service.dto.response.AvailabilityResponse;
import com.hallbooking.hall_service.dto.response.AvailableDatesResponse;
import com.hallbooking.hall_service.dto.response.HallResponse;
import com.hallbooking.hall_service.dto.response.RoomResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface HallService {

    HallResponse createHall(HallRequest request, Long vendorId);

    HallResponse getHall(Long id);

    HallResponse updateHall(Long hallId, HallRequest request, Long vendorId);

    String deleteHall(Long hallId, Long vendorId);

    RoomResponse addRoom(Long hallId, RoomRequest request, Long vendorId);

    RoomResponse updateRoom(Long roomId, RoomRequest request, Long vendorId);

    String deleteRoom(Long roomId, Long vendorId);

    List<RoomResponse> getRoomsByHall(Long hallId);

    List<HallResponse> searchHalls(double lat, double lng, double radius);

    AvailabilityResponse isAvailable(Long hallId, String dateStr);

    AvailableDatesResponse getAvailableDates(Long hallId, int days);

    List<HallResponse> getAllHalls();

    double getPricing(Long hallId, String date);

    List<String> getAvailableAmenities();

    byte[] getDocumentImage(Long id);

    String uploadImage(Long hallId, MultipartFile image, String description, Long vendorId) throws IOException;

    String uploadVirtualTour(Long hallId, MultipartFile file, String externalUrl, Long vendorId) throws IOException;

    List<HallResponse> getHallsByVendor(Long vendorId);
}
