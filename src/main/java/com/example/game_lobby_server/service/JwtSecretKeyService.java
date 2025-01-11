package com.example.game_lobby_server.service;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class JwtSecretKeyService {
    private final String secretKey;

    public JwtSecretKeyService() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[64];
        random.nextBytes(key);
        this.secretKey = Base64.getEncoder().encodeToString(key);
    }

    public String getSecretKey() {
        return secretKey;
    }
}
