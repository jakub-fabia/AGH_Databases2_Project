package edu.agh.hotel.backend.dbobjects;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookingRoomId implements java.io.Serializable {
    private Long bookingId;
    private Long roomId;
}