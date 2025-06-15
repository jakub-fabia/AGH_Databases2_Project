package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.*;
import edu.agh.hotel.backend.dto.booking.BookingCreateRequest;
import edu.agh.hotel.backend.dto.booking.BookingMapper;
import edu.agh.hotel.backend.dto.booking.BookingUpdateRequest;
import edu.agh.hotel.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final BookingRoomRepository bookingRoomRepo;
    private final GuestRepository guestRepo;
    private final HotelRepository hotelRepo;
    private final RoomRepository roomRepo;
    private final BookingMapper bookingMapper;

    @Transactional(readOnly = true)
    @Override
    public Booking get(Integer id) {
        return bookingRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking " + id + " not found"));
    }

    @Transactional
    @Override
    public Booking create(BookingCreateRequest req) {
        Booking booking = bookingMapper.toEntity(req);

        Guest guest = guestRepo.findById(req.guestId())
                .orElseThrow(() -> new EntityNotFoundException("Guest " + req.guestId() + " not found"));
        booking.setGuest(guest);

        Hotel hotel = hotelRepo.findById(Long.valueOf(req.hotelId()))
                .orElseThrow(() -> new EntityNotFoundException("Hotel " + req.hotelId() + " not found"));
        booking.setHotel(hotel);

        BigDecimal total = BigDecimal.ZERO;

        for (var brReq : req.bookingRooms()) {
            Room room = roomRepo.findById(brReq.roomId())
                    .orElseThrow(() -> new EntityNotFoundException("Room " + brReq.roomId() + " not found"));

            boolean overlap = bookingRoomRepo.existsByRoom_IdAndCheckinDateLessThanAndCheckoutDateGreaterThan(
                    room.getId(),
                    brReq.checkoutDate(),
                    brReq.checkinDate()
            );
            if (overlap) {
                throw new IllegalStateException(
                        "Room " + room.getId() + " is not available from "
                                + brReq.checkinDate() + " to " + brReq.checkoutDate()
                );
            }

            long nights = ChronoUnit.DAYS.between(brReq.checkinDate(), brReq.checkoutDate());
            BigDecimal line = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
            total = total.add(line);

            BookingRoom br = new BookingRoom(booking, room, brReq.checkinDate(), brReq.checkoutDate());
            booking.addBookingRoom(br);
        }
        booking.setCreatedAt(Instant.now());
        booking.setTotalPrice(total);
        Booking saved = bookingRepo.save(booking);
        log.info("Created Booking {} for Guest {}", saved.getId(), guest.getId());
        return saved;
    }

    @Transactional
    @Override
    public Booking update(Integer id, BookingUpdateRequest req) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking " + id + " not found"));

        bookingMapper.updateEntityFromDto(req, booking);

        if (req.bookingRooms() != null) {
            booking.getBookingRooms().clear();
            BigDecimal total = BigDecimal.ZERO;

            for (var brReq : req.bookingRooms()) {
                Room room = roomRepo.findById(brReq.roomId())
                        .orElseThrow(() -> new EntityNotFoundException("Room " + brReq.roomId() + " not found"));

                boolean overlap = bookingRoomRepo
                        .existsByRoom_IdAndBooking_IdNotAndCheckinDateLessThanAndCheckoutDateGreaterThan(
                                room.getId(),
                                booking.getId(),
                                brReq.checkoutDate(),
                                brReq.checkinDate()
                        );
                if (overlap) {
                    throw new IllegalStateException(
                            "Room " + room.getId() + " is not available from "
                                    + brReq.checkinDate() + " to " + brReq.checkoutDate()
                    );
                }

                long nights = ChronoUnit.DAYS.between(brReq.checkinDate(), brReq.checkoutDate());
                BigDecimal line = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
                total = total.add(line);

                BookingRoom br = new BookingRoom(booking, room, brReq.checkinDate(), brReq.checkoutDate());
                booking.addBookingRoom(br);
            }
            booking.setTotalPrice(total);
        }
        return bookingRepo.save(booking);
    }

    @Transactional
    @Override
    public Booking changeStatus(Integer id, BookingStatus status) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking " + id + " not found"));
        booking.setStatus(status);
        Booking updated = bookingRepo.save(booking);
        log.info("Booking {} status changed to {}", id, status);
        return updated;
    }
}
