package com.example.game_lobby_server.controller;

import com.example.game_lobby_server.dto.ResponseDto;
import com.example.game_lobby_server.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class LobbyController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private AuthenticationManager authenticationManager;



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


    //テスト
    @GetMapping("/home")
    public String home() {
        return "Welcome to the home page!";
    }

}