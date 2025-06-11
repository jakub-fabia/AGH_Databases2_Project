package edu.agh.hotel.backend.dto.Guest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record GuestUpdateRequest(
        @Size(max = 50)
        String firstName,

        @Size(max = 50)
        String lastName,

        @Past
        LocalDate dateOfBirth,

        @Size(max = 30)
        String country,

        @Size(max = 30)
        String city,

        String address,

        @Size(max = 20)
        String phone,

        @Email
        String email
) {}
