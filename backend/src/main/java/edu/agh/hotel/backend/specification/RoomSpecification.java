package edu.agh.hotel.backend.specification;

import edu.agh.hotel.backend.domain.BookingRoom;
import edu.agh.hotel.backend.domain.Hotel;
import edu.agh.hotel.backend.domain.Room;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoomSpecification {

    public static Specification<Room> filterBy(
            LocalDate checkin,
            LocalDate checkout,
            Integer roomTypeId,
            Short minCapacity,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String hotelCountry,
            String hotelCity,
            String hotelName,
            Integer hotelStars
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            assert query != null;
            Subquery<Long> sq = query.subquery(Long.class);
            Root<BookingRoom> sub = sq.from(BookingRoom.class);
            sq.select(cb.count(sub));
            sq.where(
                    cb.equal(sub.get("room"), root),
                    cb.lessThan(sub.get("checkinDate"), checkout),
                    cb.greaterThan(sub.get("checkoutDate"), checkin)
            );
            predicates.add(cb.equal(sq, 0L));

            if (roomTypeId != null) {
                predicates.add(cb.equal(
                        root.get("roomType").get("id"),
                        roomTypeId
                ));
            }
            if (minCapacity != null) {
                predicates.add(cb.ge(
                        root.get("capacity").as(Short.class),
                        minCapacity
                ));
            }
            if (minPrice != null) {
                predicates.add(cb.ge(
                        root.get("pricePerNight").as(BigDecimal.class),
                        minPrice
                ));
            }
            if (maxPrice != null) {
                predicates.add(cb.le(
                        root.get("pricePerNight").as(BigDecimal.class),
                        maxPrice
                ));
            }
            Join<Room, Hotel> hotel = root.join("hotel", JoinType.INNER);
            if (hotelCountry != null) {
                predicates.add(cb.equal(
                        cb.lower(hotel.get("country")),
                        hotelCountry.toLowerCase()
                ));
            }
            if (hotelCity != null) {
                predicates.add(cb.equal(
                        cb.lower(hotel.get("city")),
                        hotelCity.toLowerCase()
                ));
            }
            if (hotelName != null) {
                predicates.add(cb.like(
                        cb.lower(hotel.get("name")),
                        "%" + hotelName.toLowerCase() + "%"
                ));
            }
            if (hotelStars != null) {
                predicates.add(cb.equal(
                        hotel.get("stars"),
                        hotelStars
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
