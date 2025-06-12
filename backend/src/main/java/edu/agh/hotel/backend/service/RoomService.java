package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Room;
import edu.agh.hotel.backend.dto.room.RoomCreateRequest;
import edu.agh.hotel.backend.dto.room.RoomUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RoomService {
    public Page<Room> list(
            LocalDate checkin,
            LocalDate checkout,
            Integer roomTypeId,
            Short minCapacity,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String hotelCountry,
            String hotelCity,
            String hotelName,
            Integer hotelStars,
            Pageable pageable
    );

    Room get(Long id);

    Room create(RoomCreateRequest request);

    Room update(Long id, RoomUpdateRequest request);

    void delete(Long id);
}
