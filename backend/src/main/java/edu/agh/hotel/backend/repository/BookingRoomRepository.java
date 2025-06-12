package edu.agh.hotel.backend.repository;

import edu.agh.hotel.backend.domain.BookingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BookingRoomRepository extends JpaRepository<BookingRoom, Integer> {
    boolean existsByRoom_IdAndCheckinDateLessThanAndCheckoutDateGreaterThan(
            Integer roomId,
            LocalDate desiredCheckout,
            LocalDate desiredCheckin
    );
}
