package edu.agh.hotel.backend.dbobjects;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HotelRoomTypeId implements java.io.Serializable {
    private Long hotelId;
    private Long typeId;
}