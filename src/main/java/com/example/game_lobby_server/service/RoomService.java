package com.example.game_lobby_server.service;

import com.example.game_lobby_server.entity.RoomEntity;
import com.example.game_lobby_server.exception.RoomCreationException;
import com.example.game_lobby_server.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 4桁のパスワードを自動生成し、ハッシュ化してDBにRoomを保存する
     *
     * @param roomName 部屋名
     * @return プレーンな4桁のパスワード（ユーザーに通知用）
     */
    public String createRoom(String roomName) {
        // 同名チェック
        if (existsByName(roomName)) {
            throw new RoomCreationException("同じ名前のルームが既に存在します: " + roomName);
        }

        // 4桁の数字パスワードを生成 (例: "1234")
        String plainPassword = generateRandom4Digit();

        // ハッシュ化
        String hashedPassword = bCryptPasswordEncoder.encode(plainPassword);

        // DB登録
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setName(roomName);
        roomEntity.setPassword(hashedPassword);
        roomEntity.setCreatedAt(LocalDateTime.now());

        roomRepository.save(roomEntity);

        // プレーンパスワードを返却 (ユーザー通知用)
        return plainPassword;
    }

    private String generateRandom4Digit() {
        Random rand = new Random();
        int randomNum = rand.nextInt(9000) + 1000; // 1000〜9999
        return String.valueOf(randomNum);
    }

    /**
     * ルーム名の重複チェック
     */
    public boolean existsByName(String roomName) {
        return roomRepository.findAll().stream()
                .anyMatch(r -> r.getName().equals(roomName));
    }
    /**
     * 4桁のパスワード(平文)から、該当するルームを検索
     * ハッシュ化パスワードと matches で照合
     *
     * @param password 平文パスワード
     * @return 一致すれば RoomEntity / 無ければ null
     */
    public RoomEntity joinRoom(String password) {
        List<RoomEntity> rooms = roomRepository.findAll();
        for (RoomEntity room : rooms) {
            // bcrypt で照合
            if (bCryptPasswordEncoder.matches(password, room.getPassword())) {
                return room;  // 見つかった
            }
        }
        return null; // なし
    }

    /**
     * 全てのルームを取得
     */
    public List<RoomEntity> getAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * 指定IDのルームを取得
     */
    public RoomEntity getRoomById(int roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("指定したルームが見つかりません。ID=" + roomId));
    }

}
