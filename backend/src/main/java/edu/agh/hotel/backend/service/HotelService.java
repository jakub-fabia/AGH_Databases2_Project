package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Hotel;
import edu.agh.hotel.backend.dto.hotel.HotelCreateRequest;
import edu.agh.hotel.backend.dto.hotel.HotelUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface HotelService {

    Page<Hotel> list(String country, String city, String name, Integer stars, Pageable pageable);

    long countAvailableRooms(
            Long hotelId,
            Integer roomTypeId,
            LocalDate checkin,
            LocalDate checkout
    );

    Hotel get(Long id);

    Hotel create(HotelCreateRequest request);

    Hotel update(Long id, HotelUpdateRequest request);

    void delete(Long id);
}
