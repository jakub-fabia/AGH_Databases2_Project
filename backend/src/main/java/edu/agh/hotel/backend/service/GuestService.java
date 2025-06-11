package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Guest;
import edu.agh.hotel.backend.dto.guest.GuestCreateRequest;
import edu.agh.hotel.backend.dto.guest.GuestUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface GuestService {
    Page<Guest> list(String firstName, String lastName, String email, String phone, Pageable pageable);

    Guest get(Integer id);

    Guest create(GuestCreateRequest request);

    Guest update(Integer id, GuestUpdateRequest request);

    void delete(Integer id);
}
