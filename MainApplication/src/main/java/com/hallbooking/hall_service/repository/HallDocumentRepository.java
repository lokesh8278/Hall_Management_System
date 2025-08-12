package com.hallbooking.hall_service.repository;

import com.hallbooking.hall_service.dto.response.HallDocumentResponse;
import com.hallbooking.hall_service.entity.HallDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HallDocumentRepository extends JpaRepository<HallDocument, Long> {
    @Query("SELECT new com.hallbooking.hall_service.dto.response.HallDocumentResponse(d.id, d.fileName, d.description, CONCAT(:baseUrl, d.id, '/image')) " +
            "FROM HallDocument d WHERE d.hall.id = :hallId")
    List<HallDocumentResponse> fetchDocumentMeta(@Param("hallId") Long hallId, @Param("baseUrl") String baseUrl);
    @Query("SELECT d.id FROM HallDocument d WHERE d.hall.id = :hallId")
    List<Long> findThumbnailIdsByHallId(@Param("hallId") Long hallId);


}
