package edu.agh.hotel.backend.controller;

import edu.agh.hotel.backend.domain.Booking;
import edu.agh.hotel.backend.domain.BookingStatus;
import edu.agh.hotel.backend.dto.booking.BookingCreateRequest;
import edu.agh.hotel.backend.dto.booking.BookingUpdateRequest;
import edu.agh.hotel.backend.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Retrieve a booking by its ID.
     */
    @GetMapping("/{id}")
    public Booking get(@PathVariable Integer id) {
        return bookingService.get(id);
    }

    /**
     * Create a new booking (with its bookingRooms).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@RequestBody @Valid BookingCreateRequest request) {
        return bookingService.create(request);
    }

    /**
     * Full update of an existing booking, including replacing bookingRooms.
     */
    @PutMapping("/{id}")
    public Booking update(
            @PathVariable Integer id,
            @RequestBody @Valid BookingUpdateRequest request
    ) {
        return bookingService.update(id, request);
    }

    /**
     * Change only the status of a booking.
     */
    @PatchMapping("/{id}")
    public Booking changeStatus(
            @PathVariable Integer id,
            @RequestParam BookingStatus status
    ) {
        return bookingService.changeStatus(id, status);
    }
}
