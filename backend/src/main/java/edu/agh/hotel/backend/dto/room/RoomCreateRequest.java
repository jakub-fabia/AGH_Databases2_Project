package edu.agh.hotel.backend.dto.room;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RoomCreateRequest(
        @NotNull(message = "hotelId is required")
        Integer hotelId,

        @NotNull(message = "roomTypeId is required")
        Integer roomTypeId,

        @NotBlank @Size(max = 10)
        String roomNumber,

        @NotNull @Min(value = 1, message = "capacity must be at least 1")
        Short capacity,

        @NotNull @DecimalMin(value = "0.01", inclusive = true, message = "pricePerNight must be > 0")
        BigDecimal pricePerNight
) {}