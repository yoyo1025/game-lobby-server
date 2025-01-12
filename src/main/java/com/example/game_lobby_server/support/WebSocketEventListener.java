package com.example.game_lobby_server.support;

import com.example.game_lobby_server.controller.LobbyController;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // WebSocketが確立された（ユーザーがページを開き、接続した）タイミングで発火するイベント
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        // 接続時のイベント
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        // ChannelInterceptor で保存した userId, userName を取り出す
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        String userName = (String) headerAccessor.getSessionAttributes().get("userName");
        String roomId = headerAccessor.getFirstNativeHeader("roomId");
        System.out.println("WebSocket Connect: sessionId=" + sessionId
                + ", userId=" + userId + ", userName=" + userName
                + ", roomId=" + roomId);

        // 上限チェック
        boolean addedUser = LobbyController.addUser(sessionId, userId);
        if (!addedUser) {
            // 上限を超えている場合
            System.out.println("接続上限（" + LobbyController.MAX_CONNECTIONS + "人）に達したため、接続を拒否:" + userId);

            // 例外スローにより接続を強制的に終了させる
            throw new IllegalStateException("Connected limit reached.");
        }
        System.out.println("sessionId: " + sessionId + ", userId: " + userId);
        LobbyController.addUser(sessionId, userId);
        System.out.println("現在のプレイヤー数: " + LobbyController.connectedUsers.size());
        messagingTemplate.convertAndSend("/topic/room/" + roomId, sessionId + " has connected.");
    }

    // ブラウザを閉じたりリロードしたり、もしくは通信が途切れたりしたことでWebSocket接続が切断された時に発火するイベント
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // 切断時のイベント
        // StompHeaderAccessor: STOMPプロトコルのメッセージヘッダーを解析するためのクラス
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        // 切断したユーザーを管理リストから削除
        String userId = LobbyController.connectedUsers.get(sessionId);
        if (userId != null) {
            LobbyController.removeUser(sessionId);
            System.out.println("現在のプレイヤー数: " + LobbyController.connectedUsers.size());
            System.out.println("User Disconnected: " + userId);

            // 他のクライアントへ切断を通知
            messagingTemplate.convertAndSend("/topic/user-disconnected", userId + " has disconnected.");
        }
    }
}
