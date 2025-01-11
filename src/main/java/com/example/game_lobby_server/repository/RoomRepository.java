package com.example.game_lobby_server.repository;

import com.example.game_lobby_server.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Integer> {
}
