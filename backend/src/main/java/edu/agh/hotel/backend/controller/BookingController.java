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
     GET: /api/bookings/{id}
     * id - required
     Zebranie szczegółów zamówienia o danym id.
     */
    @GetMapping("/{id}")
    public Booking get(@PathVariable Integer id) {
        return bookingService.get(id);
    }

    /**
     POST: /api/bookings
     * Body: BookingCreateRequest JSON - required
     Dodanie nowego zamówienia.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@RequestBody @Valid BookingCreateRequest request) {
        return bookingService.create(request);
    }

    /**
     PUT: /api/bookings/{id}
     * id - required
     * Body: BookingUpdateRequest JSON - required
     Edycja zamówienia o danym id.
     */
    @PutMapping("/{id}")
    public Booking update(
            @PathVariable Integer id,
            @RequestBody @Valid BookingUpdateRequest request
    ) {
        return bookingService.update(id, request);
    }

    /**
     PATCH: /api/bookings/{id}?status={status}
     * id - required
     * status: BookingStatus - required
     Zmiana statusu zamówienia.
     */
    @PatchMapping("/{id}")
    public Booking changeStatus(
            @PathVariable Integer id,
            @RequestParam BookingStatus status
    ) {
        return bookingService.changeStatus(id, status);
    }
}
