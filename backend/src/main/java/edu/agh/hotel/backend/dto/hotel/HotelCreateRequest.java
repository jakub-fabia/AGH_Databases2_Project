package edu.agh.hotel.backend.dto.hotel;

import jakarta.validation.constraints.*;

import java.time.LocalTime;

public record HotelCreateRequest(
        @NotBlank  @Size(max = 255) String name,
        @NotBlank  @Size(max = 30)  String country,
        @NotBlank  @Size(max = 30)  String city,
        @NotBlank                   String address,
        @NotBlank  @Size(max = 20)  String phone,
        @Email     @NotBlank        String email,
        @Min(1)    @Max(5)          Short stars,
        @NotNull                    LocalTime checkinTime,
        @NotNull                    LocalTime checkoutTime
) {}