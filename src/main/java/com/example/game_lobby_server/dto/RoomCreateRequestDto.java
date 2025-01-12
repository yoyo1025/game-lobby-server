package com.example.game_lobby_server.dto;

public class RoomCreateRequestDto {
    private String roomName;

    // コンストラクタ
    public RoomCreateRequestDto() {
    }

    public RoomCreateRequestDto(String roomName) {
        this.roomName = roomName;
    }

    // Getter / Setter
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
