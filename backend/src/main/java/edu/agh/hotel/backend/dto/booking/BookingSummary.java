package edu.agh.hotel.backend.dto.booking;

import edu.agh.hotel.backend.domain.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record BookingSummary(
        Integer id,
        Integer hotelId,
        BigDecimal totalPrice,
        BookingStatus status,
        Instant createdAt,
        Set<BookingRoomSummary> bookingRooms
) {}
