package com.example.game_lobby_server.controller;

import com.example.game_lobby_server.dto.ResponseDto;
import com.example.game_lobby_server.dto.RoomCreateRequestDto;
import com.example.game_lobby_server.service.RoomService;
import com.example.game_lobby_server.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LobbyController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private RoomService roomService;


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

        // レスポンスメッセージを作成（パスワードをユーザーに教える）
        String message = "ルームが作成されました。パスワード: " + createdPassword;

        // ResponseDtoを返却 (statusは適宜変更してください)
        ResponseDto responseDto = new ResponseDto(message, "success");
        return ResponseEntity.ok(responseDto);
    }


}