package edu.agh.hotel.backend.dto;

import java.time.Instant;

public class ErrorResponse {
    private final int status;
    private final String message;
    private final Instant timestamp;

    public ErrorResponse(int status, String message, Instant timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
