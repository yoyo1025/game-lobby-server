package com.example.game_lobby_server.dto;

public class ErrorResponseDto {
    private String message;
    private String status;

    public ErrorResponseDto(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
