package com.example.game_lobby_server.service;

import com.example.game_lobby_server.entity.UserEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.game_lobby_server.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;



@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserEntityRepository userEntityRepository; // リポジトリのインジェクション


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        //貰った名前からユーザーを割り出し、DBに格納されているパスワードを復号して、入力されたパスワードと比較する
        UserEntity userEntity = userEntityRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));



        return User.withUsername(username)
                .password(userEntity.getPassword())
                .authorities("ROLE_USER") // ユーザの権限
                .build();
    }

}