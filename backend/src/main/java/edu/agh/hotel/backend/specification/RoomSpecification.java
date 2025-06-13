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

            // subquery to find any overlapping booking_room
            Subquery<Long> sq = query.subquery(Long.class);
            Root<BookingRoom> sub = sq.from(BookingRoom.class);
            sq.select(cb.count(sub));
            sq.where(
                    cb.equal(sub.get("room"), root),
                    // overlap test: sub.checkin < desiredCheckout
                    cb.lessThan(sub.get("checkinDate"), checkout),
                    // and sub.checkout > desiredCheckin
                    cb.greaterThan(sub.get("checkoutDate"), checkin)
            );
            // require that count == 0 => no overlaps
            predicates.add(cb.equal(sq, 0L));

            // 2. Room type
            if (roomTypeId != null) {
                predicates.add(cb.equal(
                        root.get("roomType").get("id"),
                        roomTypeId
                ));
            }

            // 3. Capacity >= minCapacity
            if (minCapacity != null) {
                predicates.add(cb.ge(
                        root.get("capacity").as(Short.class),
                        minCapacity
                ));
            }

            // 4. Price range
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

            // 5. Hotel attributes: join to Hotel
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
