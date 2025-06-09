package edu.agh.hotel.backend.dbobjects;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(columnDefinition = "jsonb")
    private String bookingRooms; // serialized JSON snapshot

    private String review;
    private Short reviewRating;
}
