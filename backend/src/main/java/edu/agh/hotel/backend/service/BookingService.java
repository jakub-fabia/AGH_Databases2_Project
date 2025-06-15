package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Booking;
import edu.agh.hotel.backend.domain.BookingStatus;
import edu.agh.hotel.backend.dto.booking.BookingCreateRequest;
import edu.agh.hotel.backend.dto.booking.BookingUpdateRequest;

public interface BookingService {
    Booking get(Integer id);

    Booking create(BookingCreateRequest request);

    Booking update(Integer id, BookingUpdateRequest request);

    Booking changeStatus(Integer id, BookingStatus status);

    void delete(Integer id);
}