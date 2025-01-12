package com.example.game_lobby_server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "battlerecord")
@Data
public class BattleRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int userID;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean isWin;

    @Column(nullable = false)
    private int point;

    @Column(nullable = false)
    private int ranking;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
