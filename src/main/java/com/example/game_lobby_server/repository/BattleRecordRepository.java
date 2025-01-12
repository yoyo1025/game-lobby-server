package com.example.game_lobby_server.repository;

import com.example.game_lobby_server.entity.BattleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BattleRecordRepository extends JpaRepository<BattleRecord, Integer> {
    // 特定のユーザーのバトル記録をすべて取得
    @Query("SELECT br FROM BattleRecord br WHERE br.userID = :userId")
    List<BattleRecord> findByUserId(int userId);
}
