package edu.agh.hotel.backend.dto.booking;

import edu.agh.hotel.backend.domain.BookingStatus;
import jakarta.validation.Valid;

import java.util.List;

public record BookingUpdateRequest(
        BookingStatus status,

        @Valid
        List<BookingCreateRequest.BookingRoomRequest> bookingRooms
) {}
