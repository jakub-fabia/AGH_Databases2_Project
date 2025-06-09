package edu.agh.hotel.backend.dbobjects;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotel")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hotel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelId;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 30)
    private String country;
    @Column(nullable = false, length = 30)
    private String city;
    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 20, unique = true)
    private String phone;
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Short stars;

    private BigDecimal reviewScore;

    private java.time.LocalTime checkinTime;
    private java.time.LocalTime checkoutTime;

    @OneToMany(mappedBy = "hotel")
    private List<HotelRoomType> hotelRoomTypes = new ArrayList<>();

    @OneToMany(mappedBy = "hotel")
    private List<Room> rooms = new ArrayList<>();
}