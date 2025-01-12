package com.example.game_lobby_server.dto;

import lombok.Data;


@Data
public class BattleRecordDto {
    private String playDate;  // プレイ日時
    private String role;      // 役職
    private String result;    // 勝敗
}
