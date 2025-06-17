package edu.agh.hotel.backend.specification;

import edu.agh.hotel.backend.domain.RoomLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoomLogSpecification {

    public static Specification<RoomLog> filterBy(
            Integer roomId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (roomId != null) {
                predicates.add(cb.equal(
                        root.get("room").get("id"),
                        roomId
                ));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        from
                ));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdAt"),
                        to
                ));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
