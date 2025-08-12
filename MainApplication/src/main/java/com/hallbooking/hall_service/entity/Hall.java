package com.hallbooking.hall_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hallbooking.bookingService.entity.Bookings;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.locationtech.jts.geom.Point;

import java.util.List;

@Entity
@Table(name = "halls")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "documents"})
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false)
    private Long ownerId;

    private Double baseprice;

    @Column(columnDefinition = "geometry(Point, 4326)")
    @JsonIgnore
    private Point location;

    @Transient
    public double getLatitude() {
        return location != null ? location.getY() : 0.0;
    }

    @Transient
    public double getLongitude() {
        return location != null ? location.getX() : 0.0;
    }

    private boolean isActive = true;

    // ---- Collections as indexed lists + SUBSELECT ----

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    @OrderColumn(name = "room_order")
    @Fetch(FetchMode.SUBSELECT)
    private List<Room> rooms;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
//    @OrderColumn(name = "document_order")
    @Fetch(FetchMode.SUBSELECT)
    private List<HallDocument> documents;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @OrderColumn(name = "image_order")
    @Fetch(FetchMode.SUBSELECT)
    private List<HallImage> images;

    @Column(name = "virtual_tour_url")
    private String virtualTourUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hall_amenities",
            joinColumns = @JoinColumn(name = "hall_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
//    @OrderColumn(name = "amenity_order")
    @Fetch(FetchMode.SUBSELECT)
    private List<Amenity> amenities;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bookings> bookings;
}
