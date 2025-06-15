package edu.agh.hotel.backend.repository;

import edu.agh.hotel.backend.domain.BookingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRoomRepository extends JpaRepository<BookingRoom, Integer> {
    boolean existsByRoom_IdAndCheckinDateLessThanAndCheckoutDateGreaterThan(
            Integer roomId,
            LocalDate desiredCheckout,
            LocalDate desiredCheckin
    );

    boolean existsByRoom_IdAndBooking_IdNotAndCheckinDateLessThanAndCheckoutDateGreaterThan(
            Integer roomId,
            Integer bookingId,
            LocalDate desiredCheckout,
            LocalDate desiredCheckin
    );

    List<BookingRoom> findAllByRoom_Hotel_IdAndCheckinDateLessThanEqualAndCheckoutDateGreaterThanEqual(
            Integer hotelId,
            LocalDate date1,
            LocalDate date2
    );
}
