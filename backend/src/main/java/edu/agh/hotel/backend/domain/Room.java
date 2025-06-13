package edu.agh.hotel.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(
        name = "room",
        uniqueConstraints = @UniqueConstraint(columnNames = { "hotel_id", "room_number" })
)
@Check(constraints = "price_per_night > 0 AND capacity > 0")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Room {

    /* ---------- Primary key ---------- */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    
    private Integer id;

    /* ---------- Relationships ---------- */

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_type_id", nullable = false)
    
    private RoomType roomType;

    /* ---------- Fields ---------- */

    @NotBlank
    @Size(max = 10)
    @Column(name = "room_number", nullable = false, length = 10)
    
    private String roomNumber;

    @Min(1)
    @Column(nullable = false)
    
    private Short capacity;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    
    private BigDecimal pricePerNight;

    /* ---------- Constructors ---------- */

    public Room(Hotel hotel,
                RoomType roomType,
                String roomNumber,
                Short capacity,
                BigDecimal pricePerNight) {
        this.hotel = hotel;
        this.roomType = roomType;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
    }

    /* ---------- Equality (by id) ---------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

