package edu.agh.hotel.backend.dbobjects;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;

    @Column(nullable = false, length = 50)
    private String firstName;
    @Column(nullable = false, length = 50)
    private String lastName;
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 30)
    private String country;
    @Column(nullable = false, length = 30)
    private String city;
    @Column(nullable = false)
    private String address;

    @Column(unique = true, length = 20)
    private String phone;
    @Column(unique = true, length = 255)
    private String email;

    @OneToMany(mappedBy = "guest")
    private List<Booking> bookings = new ArrayList<>();
}