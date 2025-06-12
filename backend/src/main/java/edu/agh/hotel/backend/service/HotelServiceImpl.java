package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Hotel;
import edu.agh.hotel.backend.dto.hotel.HotelCreateRequest;
import edu.agh.hotel.backend.dto.hotel.HotelMapper;
import edu.agh.hotel.backend.dto.hotel.HotelUpdateRequest;
import edu.agh.hotel.backend.repository.HotelRepository;
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
public class HotelServiceImpl implements HotelService {

    private final HotelRepository repo;
    @Qualifier("hotelMapperImpl")
    private final HotelMapper mapper;

    /* ── READ ─────────────────────────────────────────────────────── */

    @Transactional(readOnly = true)
    @Override
    public Page<Hotel> list(String country, String city, String name, Integer stars, Pageable pageable) {
        Specification<Hotel> spec = HotelSpecification.filterBy(country, city, name, stars);
        return repo.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Hotel get(Long id) {
        return repo.findById(id).orElseThrow(() -> notFound(id));
    }

    /* ── CREATE ───────────────────────────────────────────────────── */

    @Transactional
    @Override
    public Hotel create(HotelCreateRequest req) {
        Hotel entity = mapper.toEntity(req);
        Hotel saved  = repo.save(entity);
        log.info("Created Hotel {}", saved.getId());
        return saved;
    }

    /* ── UPDATE (PUT) ────────────────────────────────────────── */

    @Transactional
    @Override
    public Hotel update(Long id, HotelUpdateRequest req) {
        Hotel entity = repo.findById(id).orElseThrow(() -> notFound(id));
        mapper.updateEntityFromDto(req, entity);
        return entity;
    }

    /* ── DELETE ───────────────────────────────────────────────────── */

    @Transactional
    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw notFound(id);
        repo.deleteById(id);
        log.info("Deleted Hotel {}", id);
    }

    /* ── helper ───────────────────────────────────────────────────── */

    private EntityNotFoundException notFound(Long id) {
        return new EntityNotFoundException("Hotel " + id + " not found");
    }
}
