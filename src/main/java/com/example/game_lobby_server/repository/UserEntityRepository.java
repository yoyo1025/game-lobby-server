package com.example.game_lobby_server.repository;

import com.example.game_lobby_server.entity.User;
import com.example.game_lobby_server.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity,Integer> {
    // ユーザー名がすでに存在するかを確認
    boolean existsByName(String name);

    Optional<UserEntity> findByName(String name); // 名前でユーザーを検索


}
