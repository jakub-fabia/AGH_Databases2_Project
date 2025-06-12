package edu.agh.hotel.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import edu.agh.hotel.backend.views.GuestViews;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Data
@NoArgsConstructor
@Entity
@Table(
        name = "room_type",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@JsonView(GuestViews.WithBookings.class)
public class RoomType {

    /* ---------- Primary key ---------- */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_type_id")
    private Integer id;

    /* ---------- Fields ---------- */

    @NotBlank
    @Size(max = 50)
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /* ---------- Constructors ---------- */

    public RoomType(String name) {
        this.name = name;
    }

    /* ---------- Equality (by id) ---------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomType other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
