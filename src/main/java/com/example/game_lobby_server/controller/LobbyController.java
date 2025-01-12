package com.example.game_lobby_server.controller;

import com.example.game_lobby_server.dto.*;
import com.example.game_lobby_server.entity.RoomEntity;
import com.example.game_lobby_server.service.BattleRecordService;
import com.example.game_lobby_server.service.RoomService;
import com.example.game_lobby_server.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
public class LobbyController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private RoomService roomService;
  
    private AuthenticationManager authenticationManager;

    @Autowired
    private BattleRecordService battleRecordService;

    @PostMapping(value = "/signup")
    public ResponseEntity<ResponseDto> signup(@RequestBody UserForm user) {
        String userName = user.getName();
        String password = user.getPassword();


        // SignUpServiceクラスのsignupメソッドを呼び出す
        String responseMessage = signUpService.signup(userName, password);

        // レスポンスDTOを作成
        ResponseDto responseDto = new ResponseDto(responseMessage, "success");

        // レスポンスDTOを返す
        return ResponseEntity.ok(responseDto);
    }


    /**
     * 新規ルーム作成用のエンドポイント
     * JSON で { "roomName": "部屋名" } を受け取る
     * 4桁の数字パスワードを自動生成し、DBへ保存
     * ユーザーにはパスワードを返す
     */
    @PostMapping(value = "/make-room")
    public ResponseEntity<ResponseDto> makeRoom(@RequestBody RoomCreateRequestDto requestDto) {
        System.out.println("ルーム作成!!!!");
        // 部屋名を取得
        String roomName = requestDto.getRoomName();

        // ルームを作成 & パスワードを取得
        String createdPassword = roomService.createRoom(roomName);

        // ResponseDtoを返却 (statusは適宜変更してください)
        ResponseDto responseDto = new ResponseDto(createdPassword, "success");
        return ResponseEntity.ok(responseDto);
    }
    /**
     * ルーム参加用エンドポイント
     * JSON で { "password": "1234" } を受け取り
     * パスワードが一致するルームがあれば参加成功
     */
    @PostMapping("/room-join")
    public ResponseEntity<ResponseDto> joinRoom(@RequestBody RoomJoinRequestDto requestDto) {
        String password = requestDto.getPassword();

        RoomEntity joinedRoom = roomService.joinRoom(password);
        if (joinedRoom != null) {
            // 参加成功時、roomIdを付与して返却
            return ResponseEntity.ok(new ResponseDto("ルーム参加に成功しました", "success", joinedRoom.getId()));
        } else {
            // 参加失敗時
            return ResponseEntity.status(400).body(
                    new ResponseDto("ルーム参加に失敗しました。パスワードが違います。", "error", null)
            );
        }
    }

    /**
     * 全てのルーム情報を取得
     * GET /rooms
     */
    @GetMapping(value = "/rooms")
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        List<RoomEntity> rooms = roomService.getAllRooms();
        List<RoomResponseDto> responseList = rooms.stream()
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getName(),
                        room.getCreatedAt()
                ))
                .toList();
        return ResponseEntity.ok(responseList);
    }

    /**
     * 特定のルーム情報を取得
     * GET /rooms/{id}
     */
    @GetMapping("/rooms/{id}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable int id) {
        RoomEntity room = roomService.getRoomById(id);
        RoomResponseDto responseDto = new RoomResponseDto(
                room.getId(),
                room.getName(),
                room.getCreatedAt()
        );
        return ResponseEntity.ok(responseDto);
    }


    @GetMapping("/battlerecord/{id}")
    public ResponseEntity<BattleStatsDto> getBattleStats(@PathVariable int id) {
        BattleStatsDto responseDto  = battleRecordService.getUserBattleStats(id);

        return ResponseEntity.ok(responseDto);
    }


}