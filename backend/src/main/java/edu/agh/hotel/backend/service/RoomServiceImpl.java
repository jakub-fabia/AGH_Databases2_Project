package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Hotel;
import edu.agh.hotel.backend.domain.Room;
import edu.agh.hotel.backend.domain.RoomType;
import edu.agh.hotel.backend.dto.room.RoomCreateRequest;
import edu.agh.hotel.backend.dto.room.RoomMapper;
import edu.agh.hotel.backend.dto.room.RoomUpdateRequest;
import edu.agh.hotel.backend.repository.HotelRepository;
import edu.agh.hotel.backend.repository.RoomRepository;
import edu.agh.hotel.backend.repository.RoomTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepo;
    private final HotelRepository hotelRepo;
    private final RoomTypeRepository roomTypeRepo;
    @Qualifier("roomMapperImpl")
    private final RoomMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public Page<Room> list(
            LocalDate checkin,
            LocalDate checkout,
            Integer roomTypeId,
            Short minCapacity,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String hotelCountry,
            String hotelCity,
            String hotelName,
            Integer hotelStars,
            Pageable pageable
    ) {
        return roomRepo.findAll(
                RoomSpecification.filterBy(
                        checkin, checkout,
                        roomTypeId,
                        minCapacity,
                        minPrice, maxPrice,
                        hotelCountry, hotelCity,
                        hotelName, hotelStars
                ),
                pageable
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Room get(Long id) {
        return roomRepo.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Room " + id + " not found"));
    }

    @Transactional
    @Override
    public Room create(RoomCreateRequest req) {
        // map basic fields
        Room room = mapper.toEntity(req);

        // look up Hotel
        Hotel hotel = hotelRepo.findById(Long.valueOf(req.hotelId()))
                .orElseThrow(() -> new EntityNotFoundException("Hotel " + req.hotelId() + " not found"));
        room.setHotel(hotel);

        // look up RoomType
        RoomType type = roomTypeRepo.findById(req.roomTypeId())
                .orElseThrow(() -> new EntityNotFoundException("RoomType " + req.roomTypeId() + " not found"));
        room.setRoomType(type);

        Room saved = roomRepo.save(room);
        log.info("Created Room {} in Hotel {}", saved.getId(), hotel.getId());
        return saved;
    }

    @Transactional
    @Override
    public Room update(Long id, RoomUpdateRequest req) {
        Room room = roomRepo.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Room " + id + " not found"));

        // apply simple field updates
        mapper.updateEntityFromDto(req, room);

        // optional: change hotel association
        if (req.hotelId() != null) {
            Hotel hotel = hotelRepo.findById(Long.valueOf(req.hotelId()))
                    .orElseThrow(() -> new EntityNotFoundException("Hotel " + req.hotelId() + " not found"));
            room.setHotel(hotel);
        }

        // optional: change roomType association
        if (req.roomTypeId() != null) {
            RoomType type = roomTypeRepo.findById(req.roomTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("RoomType " + req.roomTypeId() + " not found"));
            room.setRoomType(type);
        }

        Room updated = roomRepo.save(room);
        log.info("Updated Room {}", updated.getId());
        return updated;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!roomRepo.existsById(id.intValue())) {
            throw new EntityNotFoundException("Room " + id + " not found");
        }
        roomRepo.deleteById(id.intValue());
        log.info("Deleted Room {}", id);
    }
}
