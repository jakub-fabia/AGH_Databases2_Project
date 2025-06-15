package edu.agh.hotel.backend.repository;

import edu.agh.hotel.backend.domain.RoomLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoomLogRepository
        extends JpaRepository<RoomLog, Integer>,
        JpaSpecificationExecutor<RoomLog> {
}
