package edu.agh.hotel.backend.specification;

import edu.agh.hotel.backend.domain.Guest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GuestSpecification {
    public static Specification<Guest> filterBy(
            String firstName,
            String lastName,
            String email,
            String phone
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (firstName != null) {
                predicates.add(cb.equal(
                        cb.lower(root.get("firstName")),
                        firstName.toLowerCase()
                ));
            }
            if (lastName != null) {
                predicates.add(cb.equal(
                        cb.lower(root.get("lastName")),
                        lastName.toLowerCase()
                ));
            }
            if (email != null) {
                predicates.add(cb.equal(
                        cb.lower(root.get("email")),
                        email.toLowerCase()
                ));
            }
            if (phone != null) {
                predicates.add(cb.equal(
                        cb.lower(root.get("phone")),
                        phone.toLowerCase()
                ));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
