package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Guest;
import edu.agh.hotel.backend.dto.guest.GuestCreateRequest;
import edu.agh.hotel.backend.dto.guest.GuestSummary;
import edu.agh.hotel.backend.dto.guest.GuestUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuestService {
    Page<GuestSummary> list(String firstName, String lastName, String email, String phone, Pageable pageable);

    Guest getBookings(Integer id);

    GuestSummary get(Integer id);

    Guest create(GuestCreateRequest request);

    Guest update(Integer id, GuestUpdateRequest request);

    void delete(Integer id);
}
