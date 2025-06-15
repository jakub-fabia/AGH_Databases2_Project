package edu.agh.hotel.backend.repository;

import edu.agh.hotel.backend.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Integer>, JpaSpecificationExecutor<Guest> {
    @Query("""
      select g
      from Guest g
      left join fetch g.bookings b
      left join fetch b.bookingRooms br
      where g.id = :id
      """)
    Optional<Guest> findWithBookingsAndRoomsById(@Param("id") Integer id);
}