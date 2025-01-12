package com.example.game_lobby_server.dto;

import java.time.LocalDateTime;

public class RoomResponseDto {
    private int id;
    private String name;
    private LocalDateTime createdAt;

    // コンストラクタ
    public RoomResponseDto(int id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    // getter
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
