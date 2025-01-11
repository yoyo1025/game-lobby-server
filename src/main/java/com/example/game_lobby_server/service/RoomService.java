 package com.example.game_lobby_server.service;

import com.example.game_lobby_server.entity.RoomEntity;
import com.example.game_lobby_server.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

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
        // 4桁の数字パスワードを生成
        String plainPassword = generateRandom4Digit();

        // パスワードをハッシュ化
        String hashedPassword = bCryptPasswordEncoder.encode(plainPassword);

        // RoomEntityを作成
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setName(roomName);
        roomEntity.setPassword(hashedPassword); // ハッシュ化したパスワードをセット
        roomEntity.setCreatedAt(LocalDateTime.now());

        // DBに保存
        roomRepository.save(roomEntity);

        // プレーンなパスワードを返却（ユーザーに通知するため）
        return plainPassword;
    }

    /**
     * 4桁の数字パスワードを生成するヘルパーメソッド
     */
    private String generateRandom4Digit() {
        Random rand = new Random();
        int randomNum = rand.nextInt(9000) + 1000; // 1000〜9999
        return String.valueOf(randomNum);
    }

    /**
     * 全てのルームを取得
     */
    public List<RoomEntity> getAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * 指定IDのルームを取得
     *
     * @param roomId ルームID
     * @return ルームエンティティ
     */
    public RoomEntity getRoomById(int roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("指定したルームが見つかりません。ID=" + roomId));
    }
}
