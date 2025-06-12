package edu.agh.hotel.backend.domain;

import com.fasterxml.jackson.annotation.*;
import edu.agh.hotel.backend.views.GuestViews;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
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
@JsonView(GuestViews.WithBookings.class)
public class BookingRoom {

    /* ---------- Primary key ---------- */

    @Id
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_room_id")
    private Integer id;

    /* ---------- Relationships ---------- */

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonBackReference
    private Booking booking;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /* ---------- Fields ---------- */

    @NotNull
    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    @NotNull
    @Column(name = "checkout_date", nullable = false)
    private LocalDate checkoutDate;

    /* ---------- Constructors ---------- */

    public BookingRoom(Booking booking,
                       Room room,
                       LocalDate checkinDate,
                       LocalDate checkoutDate) {
        this.booking      = booking;
        this.room         = room;
        this.checkinDate  = checkinDate;
        this.checkoutDate = checkoutDate;
    }

    /* ---------- Cross-field validation ---------- */

    /**
     * Enforces that checkoutDate is strictly after checkinDate.
     * This complements the database CHECK constraint.
     */
    @AssertTrue(message = "Checkout date must be after check-in date")
    @Transient
    public boolean isCheckoutAfterCheckin() {
        if (checkinDate == null || checkoutDate == null) return true;
        return checkoutDate.isAfter(checkinDate);
    }

    /* ---------- Equality (by id) ---------- */

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
