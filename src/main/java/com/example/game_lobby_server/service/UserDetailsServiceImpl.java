package com.example.game_lobby_server.service;

import com.example.game_lobby_server.entity.JWTUserDetails;
import com.example.game_lobby_server.entity.UserEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.game_lobby_server.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserEntityRepository userEntityRepository; // リポジトリのインジェクション


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userEntityRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));

        // ここでカスタムUserDetailsを作る
        return new JWTUserDetails(
                userEntity.getId(),          // userId
                userEntity.getName(),        // userName (username)
                userEntity.getPassword(),    // password (暗号化済み)
                List.of(new SimpleGrantedAuthority("ROLE_USER")) // 権限
        );
    }


}