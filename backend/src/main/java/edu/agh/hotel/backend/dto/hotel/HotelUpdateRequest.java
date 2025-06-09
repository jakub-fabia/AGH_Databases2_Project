package edu.agh.hotel.backend.dto.hotel;

import jakarta.validation.constraints.*;

import java.time.LocalTime;

public record HotelUpdateRequest(
        @Size(max = 255) String  name,
        @Size(max = 30)  String  country,
        @Size(max = 30)  String  city,
        String  address,
        @Size(max = 20)  String  phone,
        @Email           String  email,
        @Min(1) @Max(5)  Short   stars,
        LocalTime checkinTime,
        LocalTime checkoutTime
) {}
