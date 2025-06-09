package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Hotel;
import edu.agh.hotel.backend.dto.hotel.*;
import edu.agh.hotel.backend.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Hotel> list(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Hotel> list(String city, String country, Pageable pageable) {
        return repo.findByCityIgnoreCaseAndCountryIgnoreCase(city, country, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Hotel> list(short stars, Pageable pageable) {
        return repo.findByStars(stars, pageable);
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

    /* ── FULL UPDATE (PUT) ────────────────────────────────────────── */

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
