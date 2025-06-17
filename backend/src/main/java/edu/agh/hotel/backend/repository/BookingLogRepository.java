package edu.agh.hotel.backend.repository;

import edu.agh.hotel.backend.domain.BookingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BookingLogRepository
        extends JpaRepository<BookingLog, Integer>,
        JpaSpecificationExecutor<BookingLog> {
    List<BookingLog> findAllByBooking_Id(Integer bookingId);
}
