package com.example.game_lobby_server.service;

import com.example.game_lobby_server.dto.BattleRecordDto;
import com.example.game_lobby_server.dto.BattleStatsDto;
import com.example.game_lobby_server.entity.BattleRecord;
import com.example.game_lobby_server.repository.BattleRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BattleRecordService {
    private final BattleRecordRepository repository;

    public BattleRecordService(BattleRecordRepository repository) {
        this.repository = repository;
    }

    public BattleStatsDto getUserBattleStats(int userId) {
        List<BattleRecord> records = repository.findByUserId(userId);

        // バトルの詳細リストを作成
        List<BattleRecordDto> battleDetails = records.stream().map(record -> {
            BattleRecordDto dto = new BattleRecordDto();
            dto.setPlayDate(record.getCreatedAt().toString()); // 日時を文字列に変換
            dto.setRole(record.getRole());
            dto.setResult(record.isWin() ? "勝ち" : "負け");
            return dto;
        }).collect(Collectors.toList());

        // 総試合数、勝利回数、鬼での勝利回数を計算
        int totalMatches = records.size();
        int totalWins = (int) records.stream().filter(BattleRecord::isWin).count();
        int demonWins = (int) records.stream()
                .filter(record -> record.isWin() && "鬼".equals(record.getRole()))
                .count();

        // 結果を DTO に設定
        BattleStatsDto stats = new BattleStatsDto();
        stats.setBattles(battleDetails);
        stats.setTotalMatches(totalMatches);
        stats.setTotalWins(totalWins);
        stats.setDemonWins(demonWins);

        return stats;
    }
}
