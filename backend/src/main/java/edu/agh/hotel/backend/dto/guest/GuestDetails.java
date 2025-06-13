package edu.agh.hotel.backend.dto.guest;

import edu.agh.hotel.backend.dto.booking.BookingSummary;

import java.time.LocalDate;
import java.util.Set;

public record GuestDetails(
        Integer id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String country,
        String city,
        String address,
        String phone,
        String email,
        Set<BookingSummary> bookings
) {}
