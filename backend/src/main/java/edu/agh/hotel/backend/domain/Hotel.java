package edu.agh.hotel.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Objects;

@NoArgsConstructor
@Data
@Entity
@Table(name = "hotel")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Integer id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 30)
    private String country;

    @NotBlank
    @Size(max = 30)
    private String city;

    @NotBlank
    @Column(columnDefinition = "text")
    private String address;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @Min(1) @Max(5)
    private Short stars;

    @Column(name = "checkin_time", nullable = false)
    private LocalTime checkinTime;

    @Column(name = "checkout_time", nullable = false)
    private LocalTime checkoutTime;

    public Hotel(String name, String country, String city, String address,
                 String phone, Short stars,
                 LocalTime checkinTime, LocalTime checkoutTime) {

        this.name = name;
        this.country = country;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.stars = stars;
        this.checkinTime = checkinTime;
        this.checkoutTime = checkoutTime;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hotel other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}