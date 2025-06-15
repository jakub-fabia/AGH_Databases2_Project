package edu.agh.hotel.backend.controller;

import edu.agh.hotel.backend.domain.Booking;
import edu.agh.hotel.backend.domain.Hotel;
import edu.agh.hotel.backend.SuccessResponse;
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

    /**
     GET: /api/hotels/{id}
     * id - required
     Zebranie danych hotelu o podanym id.
     */
    @GetMapping
    public Page<Hotel> list(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer stars,
            @ParameterObject Pageable pageable) {
        return service.list(country, city, name, stars, pageable);
    }

    /**
     GET: /api/hotels/{id}/occupancy?date={}
     * id - required
     * date - required
     Zbiera dane o rezerwacjach (i zarezerwowanych pokojach) w danym dniu i danym hotelu.
     */
    @GetMapping("/{id}/occupancy")
    public List<Booking> getOccupancy(
            @PathVariable Long id,
            @RequestParam
            @DateTimeFormat(iso = DATE)
            LocalDate date
    ) {
        return service.listOccupancy(id, date);
    }

    /**
     GET: /api/hotels/{id}/available?checkin={}&checkout={}&roomTypeId={}
     * id - required
     * checkin - optional
     * checkout - optional
     * roomTypeId - optional
     Zwraca ilość dostępnych pokojów w danych hotelu, między datami zameldowania i wymeldowania. Opcjonalnie danego typu.
     */
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

    /**
     GET: /api/hotels/{id}
     * id - required
     Zwraca szczegóły hotelu o danym id.
     */
    @GetMapping("/{id}")
    public Hotel get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     POST: /api/hotels
     * body: HotelCreateRequest JSON - required
     Tworzy hotel.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Hotel create(@Valid @RequestBody HotelCreateRequest body) {
        return service.create(body);
    }

    /**
     PUT: /api/hotels/{id}
     * id - required
     * body: HotelUpdateRequest
     Aktualizuje szczegóły hotelu o podanym id.
     */
    @PutMapping("/{id}")
    public Hotel update(@PathVariable Long id,
                           @Valid @RequestBody HotelUpdateRequest body) {
        return service.update(id, body);
    }

    /**
     DELETE: /api/hotels/{id}
     * id - required
     Usuwa hotel o podanym id.
     */
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