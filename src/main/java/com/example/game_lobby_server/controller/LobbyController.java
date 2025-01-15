package com.example.game_lobby_server.controller;

import com.example.game_lobby_server.dto.*;
import com.example.game_lobby_server.entity.RoomEntity;
import com.example.game_lobby_server.service.BattleRecordService;
import com.example.game_lobby_server.service.RoomService;
import com.example.game_lobby_server.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:8080",
        "http://172.31.120.116:3000",
        "http://172.31.125.54:3000",
        "http://172.30.161.76:3000",
        "http://172.31.94.191:3000"
})
public class LobbyController {

    public final SimpMessagingTemplate messagingTemplate;

    // 接続中のuserIdを保存(sessionId -> userId)
    public static final ConcurrentHashMap<String, String> connectedUsers = new ConcurrentHashMap<>();

    // 接続上限を定義
    public static final int MAX_CONNECTIONS = 4;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private RoomService roomService;
  
    private AuthenticationManager authenticationManager;

    public LobbyController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
  
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

    // 接続中かチェック
    public static boolean checkDuplicatedUser(String userId) {
        return connectedUsers.containsValue(userId);
    }

    // ユーザーを追加する際に、上限に達していたら false を返す
    public static boolean addUser(String sessionId, String userId) {
        if (connectedUsers.size() >= MAX_CONNECTIONS) {
            return false;
        }
        connectedUsers.put(sessionId, userId);
        return true;
    }

    public static void removeUser(String sessionId) {
        connectedUsers.remove(sessionId);
    }

    @GetMapping("/battlerecord/{id}")
    public ResponseEntity<BattleStatsDto> getBattleStats(@PathVariable int id) {
        BattleStatsDto responseDto  = battleRecordService.getUserBattleStats(id);

        return ResponseEntity.ok(responseDto);
    }
}