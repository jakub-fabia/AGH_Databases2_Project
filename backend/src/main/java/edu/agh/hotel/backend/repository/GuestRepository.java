package edu.agh.hotel.backend.repository;

import edu.agh.hotel.backend.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Integer> { }