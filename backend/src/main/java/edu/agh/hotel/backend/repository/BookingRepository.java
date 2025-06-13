package edu.agh.hotel.backend.repository;

import edu.agh.hotel.backend.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
}
