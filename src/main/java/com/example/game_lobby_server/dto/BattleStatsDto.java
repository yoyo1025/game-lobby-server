package com.example.game_lobby_server.dto;

import lombok.Data;

import java.util.List;

@Data
public class BattleStatsDto {
    private List<BattleRecordDto> battles; // 各試合の詳細
    private int totalMatches;             // 総試合数
    private int totalWins;                // 勝利回数
    private int demonWins;                // 鬼での勝利回数
}
