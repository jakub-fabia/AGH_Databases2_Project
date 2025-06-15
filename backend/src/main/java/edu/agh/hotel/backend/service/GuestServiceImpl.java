package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Guest;
import edu.agh.hotel.backend.dto.guest.GuestCreateRequest;
import edu.agh.hotel.backend.dto.guest.GuestMapper;
import edu.agh.hotel.backend.dto.guest.GuestSummary;
import edu.agh.hotel.backend.dto.guest.GuestUpdateRequest;
import edu.agh.hotel.backend.repository.GuestRepository;
import edu.agh.hotel.backend.specification.GuestSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {

    private final GuestRepository repo;
    @Qualifier("guestMapperImpl")
    private final GuestMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public Page<GuestSummary> list(String firstName, String lastName, String email, String phone, Pageable pageable) {
        Specification<Guest> spec = GuestSpecification.filterBy(firstName, lastName, email, phone);
        Page<Guest> g = repo.findAll(spec, pageable);
        return g.map(mapper::toSummary);
    }

    @Transactional(readOnly = true)
    @Override
    public GuestSummary get(Integer id) {
        Guest g = repo.findById(id)
                .orElseThrow(() -> notFound(id));
        return mapper.toSummary(g);
    }

    @Transactional(readOnly = true)
    @Override
    public Guest getBookings(Integer id) {
        return repo.findWithBookingsAndRoomsById(id)
                .orElseThrow(() -> notFound(id));
    }

    /* ── CREATE ───────────────────────────────────────────────────── */

    @Transactional
    @Override
    public Guest create(GuestCreateRequest req) {
        Guest entity = mapper.toEntity(req);
        Guest saved = repo.save(entity);
        log.info("Created Guest {}", saved.getId());
        return saved;
    }

    /* ── FULL UPDATE (PUT) ────────────────────────────────────────── */

    @Transactional
    @Override
    public Guest update(Integer id, GuestUpdateRequest req) {
        Guest entity = repo.findById(id)
                .orElseThrow(() -> notFound(id));
        mapper.updateEntityFromDto(req, entity);
        return entity;
    }

    /* ── DELETE ───────────────────────────────────────────────────── */

    @Transactional
    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) throw notFound(id);
        repo.deleteById(id);
        log.info("Deleted Guest {}", id);
    }

    /* ── helper ───────────────────────────────────────────────────── */

    private EntityNotFoundException notFound(Integer id) {
        return new EntityNotFoundException("Guest " + id + " not found");
    }
}
