package com.example.game_lobby_server.service;

import com.example.game_lobby_server.entity.User;
import com.example.game_lobby_server.entity.UserEntity;
import com.example.game_lobby_server.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {

    @Autowired
    public BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserEntityRepository userEntityRepository;

    public String signup(String userName, String password) {


        // すでに同じユーザー名が存在しないか確認
        if (userEntityRepository.existsByName(userName)) {
            return "そのユーザー名は既に存在しています";
        }


        // パスワードを暗号化
        String encryptedPassword = bCryptPasswordEncoder.encode(password);

        // DBに登録する処理
        // 作成日とIDは勝手に挿入される
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userName);
        userEntity.setPassword(encryptedPassword);

        // ユーザー情報をDBに保存
        try {
            userEntityRepository.save(userEntity);
        } catch (Exception e) {
            throw new RuntimeException("登録失敗しました", e);
        }


        return "登録に成功しました";
    }
}