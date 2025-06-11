package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Guest;
import edu.agh.hotel.backend.dto.Guest.GuestCreateRequest;
import edu.agh.hotel.backend.dto.Guest.GuestUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface GuestService {
    Page<Guest> list(Pageable pageable);

    Guest get(Integer id);

    Guest create(GuestCreateRequest request);

    Guest update(Integer id, GuestUpdateRequest request);

    void delete(Integer id);
}
