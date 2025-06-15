package edu.agh.hotel.backend;

import java.time.Instant;

public class SuccessResponse {
    private final int status;
    private final String message;
    private final Instant timestamp;

    public SuccessResponse(int status, String message, Instant timestamp) {
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