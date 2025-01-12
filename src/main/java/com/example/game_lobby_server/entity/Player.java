package com.example.game_lobby_server.entity;

public class Player {
    private final String userId;   // or int
    private final String userName;
    private final String sessionId; // セッションID

    public Player(String userId, String userName, String sessionId) {
        this.userId = userId;
        this.userName = userName;
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return this.userId;
    }
    public String getUserName() {
        return this.userName;
    }

    public String getSessionId() {
        return this.sessionId;
    }
}
