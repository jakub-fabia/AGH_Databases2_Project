package edu.agh.hotel.backend.dbobjects;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "booking_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRoom {
    @EmbeddedId
    private BookingRoomId id;

    @ManyToOne @MapsId("bookingId")
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @MapsId("roomId")
    @JoinColumn(name = "room_id")
    private Room room;

    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private boolean breakfast;
    private boolean lateCheckout;
}