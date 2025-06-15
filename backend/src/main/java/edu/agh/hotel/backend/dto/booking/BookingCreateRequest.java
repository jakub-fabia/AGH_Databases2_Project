package edu.agh.hotel.backend.dto.booking;

import edu.agh.hotel.backend.domain.BookingStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record BookingCreateRequest(
        @NotNull(message = "guestId is required")
        Integer guestId,

        @NotNull(message = "hotelId is required")
        Integer hotelId,

        @NotNull(message = "status is required")
        BookingStatus status,

        @NotEmpty(message = "bookingRooms must have at least one entry")
        @Valid
        List<BookingRoomRequest> bookingRooms
) {
        public record BookingRoomRequest(
                @NotNull(message = "roomId is required")
                Integer roomId,

                @NotNull(message = "checkinDate is required")
                @FutureOrPresent(message = "checkinDate must be today or in the future")
                LocalDate checkinDate,

                @NotNull(message = "checkoutDate is required")
                @Future(message = "checkoutDate must be in the future")
                LocalDate checkoutDate
        ) {}
}
