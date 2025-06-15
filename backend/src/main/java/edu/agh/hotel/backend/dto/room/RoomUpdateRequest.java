package edu.agh.hotel.backend.dto.room;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RoomUpdateRequest(
        @Size(max = 10)
        String roomNumber,

        @Min(value = 1, message = "capacity must be at least 1")
        Short capacity,

        @DecimalMin(value = "0.01", inclusive = true, message = "pricePerNight must be > 0")
        BigDecimal pricePerNight,

        Integer hotelId,
        Integer roomTypeId
) {}
