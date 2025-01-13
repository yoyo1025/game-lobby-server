package com.example.game_lobby_server.service;

import com.example.game_lobby_server.entity.Player;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomPlayerManager {

    // ConcurrentHashMap: スレッドセーフにアクセス可能
    private final ConcurrentHashMap<Integer, Set<Player>> roomPlayers = new ConcurrentHashMap<>();

    // 全ての roomId を返すメソッドを追加
    public Set<Integer> getAllRoomIds() {
        return roomPlayers.keySet();
    }


    public synchronized boolean addPlayer(int roomId, Player player) {
        roomPlayers.putIfAbsent(roomId, ConcurrentHashMap.newKeySet());
        Set<Player> players = roomPlayers.get(roomId);
        if (players.size() >= 4) {
            return false; // 4人以上は参加不可
        }
        players.add(player);
        return true;
    }

    public synchronized void removePlayer(int roomId, String sessionId) {
        Set<Player> players = roomPlayers.get(roomId);
        if (players != null) {
            players.removeIf(p -> p.getSessionId().equals(sessionId));
            if (players.isEmpty()) {
                // 0人になったら削除してもよい
                roomPlayers.remove(roomId);
            }
        }
    }

    public Set<Player> getPlayers(int roomId) {
        return roomPlayers.getOrDefault(roomId, Set.of());
    }

    public int getPlayerCount(int roomId) {
        return getPlayers(roomId).size();
    }
}