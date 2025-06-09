package edu.agh.hotel.backend.repository;

import edu.agh.hotel.backend.domain.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Page<Hotel> findByStars(short stars, Pageable pageable);

    Page<Hotel> findByCityIgnoreCaseAndCountryIgnoreCase(String city, String country, Pageable pageable);

    Page<Hotel> findAll(Pageable pageable);
}
