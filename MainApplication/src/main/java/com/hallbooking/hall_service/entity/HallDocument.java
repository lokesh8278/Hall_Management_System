package com.hallbooking.hall_service.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

    @Entity
    @Table(name = "hall_documents")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class HallDocument {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String fileName;
        private String description;

        @Lob
        @Basic(fetch = FetchType.LAZY) // or remove entirely
        @Column(length = Integer.MAX_VALUE)
        @JsonIgnore
        private byte[] fileData;

        @ManyToOne
        @JoinColumn(name = "hall_id")
        private Hall hall;

    }


