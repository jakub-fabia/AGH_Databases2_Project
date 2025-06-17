package edu.agh.hotel.backend.specification;

import edu.agh.hotel.backend.domain.BookingLog;
import edu.agh.hotel.backend.domain.BookingStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingLogSpecification {

    public static Specification<BookingLog> filterBy(
            Integer bookingId,
            LocalDateTime from,
            LocalDateTime to,
            BookingStatus status
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (bookingId != null) {
                predicates.add(cb.equal(
                        root.get("booking").get("id"),
                        bookingId
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
            if (status != null) {
                predicates.add(cb.equal(
                        root.get("status"),
                        status
                ));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
