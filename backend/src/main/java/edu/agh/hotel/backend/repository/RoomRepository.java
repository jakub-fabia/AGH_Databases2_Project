package edu.agh.hotel.backend.repository;

import edu.agh.hotel.backend.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface RoomRepository extends JpaRepository<Room, Integer>, JpaSpecificationExecutor<Room> {
    @Query("""
      select count(r)
      from Room r
      where r.hotel.id = :hotelId
        and r.roomType.id = :roomTypeId
        and not exists (
          select br
          from BookingRoom br
          where br.room = r
            and br.checkinDate < :checkout
            and br.checkoutDate > :checkin
        )
      """)
    Long countAvailable(
            @Param("hotelId")   Long hotelId,
            @Param("roomTypeId") Integer roomTypeId,
            @Param("checkin") LocalDate checkin,
            @Param("checkout")  LocalDate checkout
    );
}
