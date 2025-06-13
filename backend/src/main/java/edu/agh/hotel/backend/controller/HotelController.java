package edu.agh.hotel.backend.controller;

import edu.agh.hotel.backend.domain.Booking;
import edu.agh.hotel.backend.domain.Hotel;
import edu.agh.hotel.backend.dto.SuccessResponse;
import edu.agh.hotel.backend.dto.hotel.HotelCreateRequest;
import edu.agh.hotel.backend.dto.hotel.HotelUpdateRequest;
import edu.agh.hotel.backend.service.HotelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService service;

    /* ── LIST ─────────────────────────────────────────────────────── */

    @GetMapping
    public Page<Hotel> list(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer stars,
            @ParameterObject Pageable pageable) {
        return service.list(country, city, name, stars, pageable);
    }

    @GetMapping("/{hotelId}/occupancy")
    public List<Booking> getOccupancy(
            @PathVariable Long hotelId,
            @RequestParam
            @DateTimeFormat(iso = DATE)
            @FutureOrPresent(message = "date must be today or in the future")
            LocalDate date
    ) {
        return service.listOccupancy(hotelId, date);
    }

    /* ── COUNT AVAILABLE ROOMS ────────────────────────────────────── */

    @GetMapping("/{id}/available")
    public long count(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @FutureOrPresent LocalDate checkin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @FutureOrPresent LocalDate checkout,
            @RequestParam(required = false) Integer roomTypeId
    ) {
        return service.countAvailableRooms(id, roomTypeId, checkin, checkout);
    }


    /* ── GET ONE ──────────────────────────────────────────────────── */

    @GetMapping("/{id}")
    public Hotel get(@PathVariable Long id) {
        return service.get(id);
    }

    /* ── CREATE ───────────────────────────────────────────────────── */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Hotel create(@Valid @RequestBody HotelCreateRequest body) {
        return service.create(body);
    }

    /* ── UPDATE (PUT) ────────────────────────────────────────── */

    @PutMapping("/{id}")
    public Hotel update(@PathVariable Long id,
                           @Valid @RequestBody HotelUpdateRequest body) {
        return service.update(id, body);
    }

    /* ── DELETE ───────────────────────────────────────────────────── */

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> delete(@PathVariable Long id) {
        service.delete(id);
        SuccessResponse success = new SuccessResponse(
                HttpStatus.OK.value(),
                "Hotel deleted successfully with id " + id,
                Instant.now()
        );
        return ResponseEntity.ok(success);
    }
}