package edu.agh.hotel.backend.controller;

import edu.agh.hotel.backend.SuccessResponse;
import edu.agh.hotel.backend.domain.Guest;
import edu.agh.hotel.backend.dto.guest.GuestCreateRequest;
import edu.agh.hotel.backend.dto.guest.GuestSummary;
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

    /**
     GET: /api/guests?firstName={}&lastName={}&email={}&phone={}
     * firstName - optional
     * lastName - optional
     * email - optional
     * phone - optional
     Szukanie gościa o podanych danych.
     */
    @GetMapping
    public Page<GuestSummary> list(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @ParameterObject Pageable pageable) {
        return service.list(firstName, lastName, email, phone, pageable);
    }

    /**
     GET: /api/guests/{id}
     * id - required
     Zebarnie szczegółow gościa o podanym id z jego rezerwacjami.
     */
    @GetMapping("/bookings/{id}")
    public Guest getBookings(@PathVariable Integer id) {
        return service.getBookings(id);
    }

    /**
     GET: /api/guests/{id}
     * id - required
     Zebranie szczegółów gościa o podanym id bez rezerwacji.
     */
    @GetMapping("/{id}")
    public GuestSummary get(@PathVariable Integer id) {
        return service.get(id);
    }

    /**
     POST: /api/guests
     * body: GuestCreateRequest JSON - required
     Utworzenie nowego gościa.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Guest create(@Valid @RequestBody GuestCreateRequest body) {
        return service.create(body);
    }

    /**
     PUT: /api/guests/{id}
     * id - required
     * body: GuestUpdateRequest JSON - required
     Aktualizacja szczegółów gościa o podanym id.
     */
    @PutMapping("/{id}")
    public Guest update(@PathVariable Integer id,
                        @Valid @RequestBody GuestUpdateRequest body) {
        return service.update(id, body);
    }

    /**
     DELETE: /api/guests/{id}
     * id - required
     Usunięcie gościa o podanym id.
     */
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