package edu.agh.hotel.backend.repository;

import edu.agh.hotel.backend.domain.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
}