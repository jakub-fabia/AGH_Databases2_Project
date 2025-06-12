package edu.agh.hotel.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import edu.agh.hotel.backend.Views.GuestViews;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "guest")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Guest {

    /* ---------- Primary key ---------- */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    @JsonView(GuestViews.Summary.class)
    private Integer id;

    /* ---------- Required fields ---------- */

    @NotBlank
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    @JsonView(GuestViews.Summary.class)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    @JsonView(GuestViews.Summary.class)
    private String lastName;

    @NotNull
    @Past
    @Column(name = "date_of_birth", nullable = false)
    @JsonView(GuestViews.Summary.class)
    private LocalDate dateOfBirth;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    @JsonView(GuestViews.Summary.class)
    private String country;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    @JsonView(GuestViews.Summary.class)
    private String city;

    @NotBlank
    @Column(nullable = false, columnDefinition = "text")
    @JsonView(GuestViews.Summary.class)
    private String address;

    @Size(max = 20)
    @Column(length = 20)
    @JsonView(GuestViews.Summary.class)
    private String phone;

    @Email
    @Size(max = 255)
    @Column(length = 255)
    @JsonView(GuestViews.Summary.class)
    private String email;

    @OneToMany(
        mappedBy = "guest",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonManagedReference
    @JsonView(GuestViews.WithBookings.class)
    private Set<Booking> bookings = new HashSet<>();


    public Guest(String firstName, String lastName, LocalDate dateOfBirth,
                 String country, String city, String address,
                 String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.country = country;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Guest other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}