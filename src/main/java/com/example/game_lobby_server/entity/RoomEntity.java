package com.example.game_lobby_server.entity;

public class RoomEntity {
    // ルームID
    public int id;
    // ルーム名
    public String name;

    // 入室パスワード
    public String password;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
