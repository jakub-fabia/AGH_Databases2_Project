package edu.agh.hotel.backend.dto.room;

import java.math.BigDecimal;

public record RoomSummary(
        Integer id,
        Integer hotelId,
        String roomNumber,
        Short capacity,
        BigDecimal pricePerNight,
        Integer roomTypeId,
        String roomTypeName
) {}
