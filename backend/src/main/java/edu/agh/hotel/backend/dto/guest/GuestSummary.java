package edu.agh.hotel.backend.dto.guest;

import java.time.LocalDate;

public record GuestSummary(
        Integer id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String country,
        String city,
        String address,
        String phone,
        String email
) {}