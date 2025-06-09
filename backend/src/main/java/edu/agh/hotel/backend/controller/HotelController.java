package edu.agh.hotel.backend.controller;

import edu.agh.hotel.backend.domain.Hotel;
import edu.agh.hotel.backend.dto.SuccessResponse;
import edu.agh.hotel.backend.dto.hotel.HotelCreateRequest;
import edu.agh.hotel.backend.dto.hotel.HotelUpdateRequest;
import edu.agh.hotel.backend.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService service;

    /* ── LIST ─────────────────────────────────────────────────────── */

    @GetMapping("/all")
    public Page<Hotel> list(
            @ParameterObject Pageable pageable) {
        return service.list(pageable);
    }

    @GetMapping
    public Page<Hotel> list(
            @RequestParam String city,
            @RequestParam String country,
            @ParameterObject Pageable pageable) {
        return service.list(city, country, pageable);
    }

    @GetMapping("/stars/{stars}")
    public Page<Hotel> listByStars(
            @PathVariable short stars,
            @ParameterObject Pageable pageable) {
        return service.list(stars, pageable);
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