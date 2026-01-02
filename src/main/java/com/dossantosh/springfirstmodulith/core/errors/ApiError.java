package com.dossantosh.springfirstmodulith.core.errors;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a standardized API error response.
 * Contains HTTP status, error type, detailed message, request path,
 * optional details, and a timestamp of when the error occurred.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> details;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Constructs an ApiError with given status, error, message, and path.
     * The timestamp is set to the current date and time.
     *
     * @param status  HTTP status code (e.g., 404, 400, 500)
     * @param error   Error type description (e.g., "Not Found", "Bad Request")
     * @param message Detailed error message
     * @param path    Request URI path where the error occurred
     */
    public ApiError(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
