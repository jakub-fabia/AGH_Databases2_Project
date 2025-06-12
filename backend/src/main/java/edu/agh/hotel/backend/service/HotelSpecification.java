package edu.agh.hotel.backend.service;

import edu.agh.hotel.backend.domain.Hotel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class HotelSpecification {
    public static Specification<Hotel> filterBy(
            String country,
            String city,
            String name,
            Integer stars
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (country != null) {
                predicates.add(cb.equal(
                        cb.lower(root.get("country")),
                        country.toLowerCase()
                ));
            }

            if (city != null) {
                predicates.add(cb.equal(
                        cb.lower(root.get("city")),
                        city.toLowerCase()
                ));
            }

            if (name != null) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                ));
            }

            if (stars != null) {
                predicates.add(cb.equal(root.get("stars"), stars));
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
