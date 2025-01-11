package com.example.game_lobby_server.dto;

public class ResponseDto {

    private String message;
    private String status;
    private Integer roomId;  // 追加: roomId

    // コンストラクタ（roomId あり）
    public ResponseDto(String message, String status, Integer roomId) {
        this.message = message;
        this.status = status;
        this.roomId = roomId;
    }

    // コンストラクタ（roomId なし）
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

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
}
