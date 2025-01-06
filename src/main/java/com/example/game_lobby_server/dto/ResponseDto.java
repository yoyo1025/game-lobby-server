package com.example.game_lobby_server.dto;

public class ResponseDto {

    private String message;
    private String status;

    // コンストラクタ
    public ResponseDto(String message, String status) {
        this.message = message;
        this.status = status;
    }

    // ゲッターとセッター
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
