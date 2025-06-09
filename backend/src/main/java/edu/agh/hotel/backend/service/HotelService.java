package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Hotel;
import edu.agh.hotel.backend.dto.hotel.HotelCreateRequest;
import edu.agh.hotel.backend.dto.hotel.HotelUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HotelService {

    Page<Hotel> list(Pageable pageable);

    Page<Hotel> list(String city, String country, Pageable pageable);

    Page<Hotel> list(short stars, Pageable pageable);

    Hotel get(Long id);

    Hotel create(HotelCreateRequest request);

    Hotel update(Long id, HotelUpdateRequest request);

    void delete(Long id);
}
