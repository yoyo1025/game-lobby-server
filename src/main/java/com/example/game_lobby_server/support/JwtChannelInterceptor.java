package com.example.game_lobby_server.support;

import com.example.game_lobby_server.service.JwtSecretKeyService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtSecretKeyService jwtSecretKeyService;

    public JwtChannelInterceptor(JwtSecretKeyService jwtSecretKeyService) {
        this.jwtSecretKeyService = jwtSecretKeyService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // STOMP CONNECT コマンドの場合のみ処理
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            System.out.println("Received STOMP CONNECT command");
            System.out.println("Headers: " + accessor.toNativeHeaderMap()); // すべてのヘッダーを出力
            System.out.println("Interceptor secretKey: " + jwtSecretKeyService.getSecretKey());


            // ヘッダーから Authorization を取得
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // JWT をパース
                try {
                    Claims claims = Jwts.parser()
                            .setSigningKey(jwtSecretKeyService.getSecretKey().getBytes())
                            .parseClaimsJws(token)
                            .getBody();

                    // JWTの情報から userId, userName などを取得（Claimsの取り出し方は実装により異なる）
                    String userName = claims.getSubject(); // 例: subjectをユーザー名としている場合
                    String userId = claims.get("userId").toString();  // 安全に Integer を String に変換

                    // Session Attributes に保存しておくと、後続の SessionConnectEvent で利用可能
                    accessor.getSessionAttributes().put("userId", userId);
                    accessor.getSessionAttributes().put("userName", userName);

                } catch (Exception e) {
                    System.out.println("JWT parse error: " + e.getMessage());  // 例外内容を出力
                    // JWTが無効な場合は接続を拒否するなど
                    throw new IllegalArgumentException("Invalid JWT token", e);
                }
            }
        }

        return message;
    }
}
