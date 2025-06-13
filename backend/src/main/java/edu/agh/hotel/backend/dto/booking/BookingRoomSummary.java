package edu.agh.hotel.backend.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingRoomSummary(
        Integer id,
        Integer roomId,
        String roomNumber,
        Integer hotelId,
        Integer roomTypeId,
        String roomTypeName,
        Short capacity,
        BigDecimal pricePerNight,
        LocalDate checkinDate,
        LocalDate checkoutDate
) {}