package edu.agh.hotel.backend.controller;

import edu.agh.hotel.backend.domain.Room;
import edu.agh.hotel.backend.dto.room.RoomCreateRequest;
import edu.agh.hotel.backend.dto.room.RoomUpdateRequest;
import edu.agh.hotel.backend.service.RoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     GET /api/rooms?checkin={}&checkout={}&roomTypeId={}&minCapacity={}&minPrice={}&maxPrice={}&hotelCountry={}&hotelCity={}&hotelName={}&hotelStars={}
     * checkin - required
     * checkout - required
     * roomTypeId, minCapacity, minPrice, maxPrice, hotelCountry, hotelCity, hotelName, hotelStars - optional
     Wyszukiwanie dostępnych pokoi w przedziale dat z możliwością filtrowania.
     */
    @GetMapping
    public Page<Room> list(
            @RequestParam
            @DateTimeFormat(iso = DATE)
            @FutureOrPresent(message = "checkin must be today or in the future")
            LocalDate checkin,

            @RequestParam
            @DateTimeFormat(iso = DATE)
            @FutureOrPresent(message = "checkout must be today or in the future")
            LocalDate checkout,
            @RequestParam(required = false) Integer roomTypeId,
            @RequestParam(required = false) Short minCapacity,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String hotelCountry,
            @RequestParam(required = false) String hotelCity,
            @RequestParam(required = false) String hotelName,
            @RequestParam(required = false) Integer hotelStars,
            Pageable pageable
    ) {
        return roomService.list(
                checkin, checkout,
                roomTypeId, minCapacity,
                minPrice, maxPrice,
                hotelCountry, hotelCity,
                hotelName, hotelStars,
                pageable
        );
    }

    /**
     GET /api/rooms/{id}/available?checkin={}&checkout={}
     * id - required
     * checkin - required
     * checkout - required
     Sprawdzenie czy dany pokój jest dostępny w przedziale dat.
     */
    @GetMapping("/{id}/available")
    public boolean isAvailable(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @FutureOrPresent LocalDate checkin,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @FutureOrPresent LocalDate checkout
    ) {
        if (!checkin.isBefore(checkout)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "checkout date must be at least one day after checkin"
            );
        }
        return roomService.isAvailable(id, checkin, checkout);
    }

    /**
     GET /api/rooms/{id}
     * id - required
     Zebranie szczegółów o pokoju.
     */
    @GetMapping("/{id}")
    public Room get(@PathVariable Long id) {
        return roomService.get(id);
    }

    /**
     POST: /api/rooms
     * body: RoomCreateRequest JSON - required
     Stworzenie nowego pokoju.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Room create(@RequestBody @Valid RoomCreateRequest request) {
        return roomService.create(request);
    }

    /**
     PUT: /api/rooms/{id}
     * id - required
     * body: RoomUpdateRequest JSON - required
     Aktualizacja szczegółów pokoju.
     */
    @PutMapping("/{id}")
    public Room update(
            @PathVariable Long id,
            @RequestBody @Valid RoomUpdateRequest request
    ) {
        return roomService.update(id, request);
    }
}