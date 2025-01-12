package com.example.game_lobby_server.dto;

public class RoomJoinRequestDto {
    private String password;

    public RoomJoinRequestDto() {
    }

    public RoomJoinRequestDto(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
