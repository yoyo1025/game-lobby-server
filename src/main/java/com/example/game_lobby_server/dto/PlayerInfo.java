package com.example.game_lobby_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerInfo {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("isDemon")
    private boolean isDemon;

    // コンストラクタ
    public PlayerInfo(String userId, String userName, boolean isDemon) {
        this.userId = userId;
        this.userName = userName;
        this.isDemon = isDemon;
    }

    // getter, setter
    public String getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    @JsonProperty("isDemon") // ここも追加して確実に一致させる
    public boolean isDemon() {
        return isDemon;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    @JsonProperty("isDemon") // セッターにも追加
    public void setDemon(boolean demon) {
        isDemon = demon;
    }
}
