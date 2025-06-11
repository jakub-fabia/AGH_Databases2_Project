package edu.agh.hotel.backend.repository;

import edu.agh.hotel.backend.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GuestRepository extends JpaRepository<Guest, Integer>, JpaSpecificationExecutor<Guest> { }