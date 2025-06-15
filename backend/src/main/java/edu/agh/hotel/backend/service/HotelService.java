package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Booking;
import edu.agh.hotel.backend.domain.Hotel;
import edu.agh.hotel.backend.domain.Room;
import edu.agh.hotel.backend.dto.hotel.HotelCreateRequest;
import edu.agh.hotel.backend.dto.hotel.HotelUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface HotelService {

    Page<Hotel> getAll(Pageable pageable);

    Page<Hotel> list(String country, String city, String name, Integer stars, Pageable pageable);

    Page<Room> availableRooms(LocalDate checkin,
                                   LocalDate checkout,
                                   Integer roomTypeId,
                                   Short minCapacity,
                                   BigDecimal minPrice,
                                   BigDecimal maxPrice,
                                   Long hotelId,
                                   Pageable pageable
    );

    List<Booking> listOccupancy(Long hotelId, LocalDate date);

    Hotel get(Long id);

    Hotel create(HotelCreateRequest request);

    Hotel update(Long id, HotelUpdateRequest request);

    void delete(Long id);
}
