package edu.agh.hotel.backend.domain;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "booking")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Booking {

    /* ---------- Primary key ---------- */
    @Id
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer id;

    /* ---------- Relationships ---------- */

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    @JsonBackReference
    private Guest guest;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    /* ---------- Fields ---------- */

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 11, nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @NotNull
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(
        mappedBy = "booking",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonManagedReference
    private Set<BookingRoom> bookingRooms = new HashSet<>();


    /* ---------- Constructors ---------- */

    public Booking(Guest guest,
                   Hotel hotel,
                   BigDecimal totalPrice,
                   BookingStatus status) {
        this.guest = guest;
        this.hotel = hotel;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public void addBookingRoom(BookingRoom bookingRoom) {
        bookingRooms.add(bookingRoom);
        bookingRoom.setBooking(this);
    }

    /* ---------- Equality (by id) ---------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}