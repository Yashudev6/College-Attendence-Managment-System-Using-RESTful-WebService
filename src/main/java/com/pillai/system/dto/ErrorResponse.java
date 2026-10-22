package com.pillai.system.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String timestamp;
    private int status;
    private String message;
    private String path;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now().toString();
    }

    public ErrorResponse(int status, String message, String path) {
        this.timestamp = LocalDateTime.now().toString();
        this.status = status;
        this.message = message;
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
