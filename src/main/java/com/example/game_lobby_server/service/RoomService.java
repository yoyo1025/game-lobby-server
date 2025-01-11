 package com.example.game_lobby_server.service;

import com.example.game_lobby_server.entity.RoomEntity;
import com.example.game_lobby_server.exception.RoomCreationException;
import com.example.game_lobby_server.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
        if (existsByName(roomName)) {
            throw new RoomCreationException("同じ名前のルームが既に存在します: " + roomName);
        }

        // 4桁の数字パスワードを生成
        String plainPassword = generateRandom4Digit();

        // パスワードをハッシュ化
        String hashedPassword = bCryptPasswordEncoder.encode(plainPassword);

        // RoomEntityを作成
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setName(roomName);
        roomEntity.setPassword(hashedPassword);
        roomEntity.setCreatedAt(LocalDateTime.now());

        // DBに保存
        roomRepository.save(roomEntity);

        return plainPassword;  // 通知用のパスワードを返却
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

    /**
     * 同名のルームが存在するかチェック
     */
    public boolean existsByName(String roomName) {
        // findByName を使うため、リポジトリにメソッドを追加してもOK
        // ここでは例として getAllRooms() して名前を手動で探すやり方でもよい
        return roomRepository.findAll().stream()
                .anyMatch(r -> r.getName().equals(roomName));
    }

    /**
     * パスワードからルームに参加
     * @param password 4桁のパスワード
     * @return ルームが見つかればそのエンティティ、なければnull
     */
    public RoomEntity joinRoom(String password) {
        List<RoomEntity> rooms = roomRepository.findAll();  // すべてのルームを取得し、パスワードをチェック

        // ルームを一つずつ確認し、ハッシュ化パスワードと平文パスワードを照合
        for (RoomEntity room : rooms) {
            if (bCryptPasswordEncoder.matches(password, room.getPassword())) {
                return room;  // パスワードが一致した場合、そのルームを返す
            }
        }

        // 一致するルームが見つからなかった場合
        return null;
    }

}
