package edu.agh.hotel.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.agh.hotel.backend.Views.GuestViews;
import edu.agh.hotel.backend.domain.Guest;
import edu.agh.hotel.backend.dto.SuccessResponse;
import edu.agh.hotel.backend.dto.guest.GuestCreateRequest;
import edu.agh.hotel.backend.dto.guest.GuestUpdateRequest;
import edu.agh.hotel.backend.service.GuestService;
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
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService service;

    /* ── LIST ─────────────────────────────────────────────────────── */

    @GetMapping
    @JsonView(GuestViews.Summary.class)
    public Page<Guest> list(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @ParameterObject Pageable pageable) {
        return service.list(firstName, lastName, email, phone, pageable);
    }

    @GetMapping("/bookings/{id}")
    @JsonView(GuestViews.WithBookings.class)
    public Guest getBookings(@PathVariable Integer id) {
        return service.getBookings(id);
    }

    /* ── GET ONE ──────────────────────────────────────────────────── */

    @GetMapping("/{id}")
    @JsonView(GuestViews.Summary.class)
    public Guest get(@PathVariable Integer id) {
        return service.get(id);
    }

    /* ── CREATE ───────────────────────────────────────────────────── */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Guest create(@Valid @RequestBody GuestCreateRequest body) {
        return service.create(body);
    }

    /* ── UPDATE (PUT) ─────────────────────────────────────────────── */

    @PutMapping("/{id}")
    public Guest update(@PathVariable Integer id,
                        @Valid @RequestBody GuestUpdateRequest body) {
        return service.update(id, body);
    }

    /* ── DELETE ───────────────────────────────────────────────────── */

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> delete(@PathVariable Integer id) {
        service.delete(id);
        SuccessResponse success = new SuccessResponse(
                HttpStatus.OK.value(),
                "Guest deleted successfully with id " + id,
                Instant.now()
        );
        return ResponseEntity.ok(success);
    }
}

