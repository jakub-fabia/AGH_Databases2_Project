package edu.agh.hotel.backend.dto.Guest;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record GuestCreateRequest(
        @NotBlank @Size(max = 50)
        String firstName,

        @NotBlank @Size(max = 50)
        String lastName,

        @NotNull @Past
        LocalDate dateOfBirth,

        @NotBlank @Size(max = 30)
        String country,

        @NotBlank @Size(max = 30)
        String city,

        @NotBlank
        String address,

        @Size(max = 20)
        String phone,

        @Email @Size(max = 255)
        String email
) {}