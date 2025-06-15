package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Booking;
import edu.agh.hotel.backend.domain.BookingRoom;
import edu.agh.hotel.backend.domain.Hotel;
import edu.agh.hotel.backend.dto.hotel.HotelCreateRequest;
import edu.agh.hotel.backend.dto.hotel.HotelMapper;
import edu.agh.hotel.backend.dto.hotel.HotelUpdateRequest;
import edu.agh.hotel.backend.repository.BookingRoomRepository;
import edu.agh.hotel.backend.repository.HotelRepository;
import edu.agh.hotel.backend.repository.RoomRepository;
import edu.agh.hotel.backend.specification.HotelSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {
    private final RoomRepository roomRepository;
    private final BookingRoomRepository bookingRoomRepo;
    private final HotelRepository repo;
    @Qualifier("hotelMapperImpl")
    private final HotelMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public Page<Hotel> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Hotel> list(String country, String city, String name, Integer stars, Pageable pageable) {
        Specification<Hotel> spec = HotelSpecification.filterBy(country, city, name, stars);
        return repo.findAll(spec, pageable);
    }

    @Override
    public long countAvailableRooms(Long hotelId, Integer roomTypeId, LocalDate checkin, LocalDate checkout) {
        return roomRepository.countAvailable(hotelId, roomTypeId, checkin, checkout);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> listOccupancy(Long hotelId, LocalDate date) {
        List<BookingRoom> rooms = bookingRoomRepo
                .findAllByRoom_Hotel_IdAndCheckinDateLessThanEqualAndCheckoutDateGreaterThanEqual(
                        hotelId.intValue(), date, date
                );

        Map<Booking, List<BookingRoom>> byBooking = rooms.stream()
                .collect(Collectors.groupingBy(BookingRoom::getBooking));

        List<Booking> result = new ArrayList<>();
        for (Map.Entry<Booking, List<BookingRoom>> e : byBooking.entrySet()) {
            Booking booking = e.getKey();
            booking.getBookingRooms().clear();
            e.getValue().forEach(booking::addBookingRoom);
            result.add(booking);
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public Hotel get(Long id) {
        return repo.findById(id).orElseThrow(() -> notFound(id));
    }

    @Transactional
    @Override
    public Hotel create(HotelCreateRequest req) {
        Hotel entity = mapper.toEntity(req);
        Hotel saved  = repo.save(entity);
        log.info("Created Hotel {}", saved.getId());
        return saved;
    }

    @Transactional
    @Override
    public Hotel update(Long id, HotelUpdateRequest req) {
        Hotel entity = repo.findById(id).orElseThrow(() -> notFound(id));
        mapper.updateEntityFromDto(req, entity);
        return entity;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw notFound(id);
        repo.deleteById(id);
        log.info("Deleted Hotel {}", id);
    }

    private EntityNotFoundException notFound(Long id) {
        return new EntityNotFoundException("Hotel " + id + " not found");
    }
}