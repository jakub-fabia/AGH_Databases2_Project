package edu.agh.hotel.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "booking_room")
@Check(constraints = "checkout_date > checkin_date")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class BookingRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_room_id")
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonBackReference
    private Booking booking;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotNull
    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    @NotNull
    @Column(name = "checkout_date", nullable = false)
    private LocalDate checkoutDate;

    public BookingRoom(Booking booking,
                       Room room,
                       LocalDate checkinDate,
                       LocalDate checkoutDate) {
        this.booking      = booking;
        this.room         = room;
        this.checkinDate  = checkinDate;
        this.checkoutDate = checkoutDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingRoom other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}