package edu.agh.hotel.backend.controller;


import edu.agh.hotel.backend.domain.BookingLog;
import edu.agh.hotel.backend.domain.BookingStatus;
import edu.agh.hotel.backend.domain.RoomLog;
import edu.agh.hotel.backend.repository.BookingLogRepository;
import edu.agh.hotel.backend.repository.RoomLogRepository;
import edu.agh.hotel.backend.specification.BookingLogSpecification;
import edu.agh.hotel.backend.specification.RoomLogSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@RestController
@RequestMapping("/api/logs")
@Validated
@RequiredArgsConstructor
public class LogController {

    private final BookingLogRepository bookingLogRepo;
    private final RoomLogRepository roomLogRepo;

    /**
     GET /api/logs/bookings?bookingId={}&from={}&to={}
     * bookingId - optional
     * from - optional
     * to - optional
     Zebranie logów zamówień, opcjonalnie przefiltrowanych.
     */
    @GetMapping("/bookings")
    public Page<BookingLog> getBookingLogs(
            @RequestParam(required = false) Integer bookingId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DATE_TIME) Instant from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DATE_TIME) Instant to,
            @RequestParam(required = false) BookingStatus status,
            Pageable pageable
    ) {
        return bookingLogRepo.findAll(
                BookingLogSpecification.filterBy(bookingId, from, to, status),
                pageable
        );
    }

    /**
     * GET /api/logs/rooms?roomId={}&from={}&to={}
     * roomId - optional
     * from - optional
     * to - optional
     Zebranie logów pokoi, opcjonalnie przefiltrowanych.
     */
    @GetMapping("/rooms")
    public Page<RoomLog> getRoomLogs(
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DATE_TIME) Instant from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DATE_TIME) Instant to,
            Pageable pageable
    ) {
        return roomLogRepo.findAll(
                RoomLogSpecification.filterBy(roomId, from, to),
                pageable
        );
    }
}
