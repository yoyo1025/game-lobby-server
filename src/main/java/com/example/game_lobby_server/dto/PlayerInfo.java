package com.example.game_lobby_server.dto; // パッケージは適宜変更

public class PlayerInfo {
    private String userId;
    private String userName;
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
    public boolean isDemon() {
        return isDemon;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setDemon(boolean demon) {
        isDemon = demon;
    }
}
