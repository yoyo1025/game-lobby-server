package com.example.game_lobby_server.support;

import com.example.game_lobby_server.controller.LobbyController;
import com.example.game_lobby_server.dto.PlayerInfo;
import com.example.game_lobby_server.entity.Player;
import com.example.game_lobby_server.service.RoomPlayerManager;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
public class WebSocketEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomPlayerManager roomPlayerManager;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate,
                                  RoomPlayerManager roomPlayerManager) {
        this.messagingTemplate = messagingTemplate;
        this.roomPlayerManager = roomPlayerManager;
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

        Player player = new Player(userId, userName, sessionId);
        boolean added = roomPlayerManager.addPlayer(Integer.parseInt(roomId), player);
        if (!added) {
            // 4人超過なら参加拒否
            System.out.println("部屋 " + roomId + " は既に4人います。接続を拒否: " + userId);
            throw new IllegalStateException("Room is full.");
        }

        // 部屋の人数をクライアントへ通知
        broadcastRoomPlayers(Integer.parseInt(roomId));

        // もし4人揃ったらタイマーをセットするなどの処理
        if (roomPlayerManager.getPlayerCount(Integer.parseInt(roomId)) == 4) {
            startCountdownAndSendData(Integer.parseInt(roomId));
        }
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

    // 部屋のメンバー一覧をブロードキャスト
    private void broadcastRoomPlayers(int roomId) {
        Set<Player> players = roomPlayerManager.getPlayers(roomId);

        // 例: JSON に変換して /topic/room/{roomId} に送信
        messagingTemplate.convertAndSend("/topic/room/" + roomId, players);
    }

    // 指定した部屋のメンバーにゲーム画面への遷移を許可
    private void permitMoveGameScreen(int roomId) {
        System.out.println("Game screen permission sent for roomId: " + roomId);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/completed", "Game Start!");
    }


    // 4人揃ったら10秒後に外部APIへ送信
    private void startCountdownAndSendData(int roomId) {
        System.out.println("Room " + roomId + " has 4 players. Start 10-second countdown.");
        // 新しいスレッドで10秒後にAPIを呼ぶ
        new Thread(() -> {
            try {
                Thread.sleep(10_000); // 10秒待機
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 10秒後に外部APIへ送信
            sendPlayerInfoToExternalApi(roomId);
            permitMoveGameScreen(roomId);
        }).start();
    }

    private void sendPlayerInfoToExternalApi(int roomId) {
        Set<Player> players = roomPlayerManager.getPlayers(roomId);
        if (players.size() < 4) {
            // 10秒の間に誰か抜けた場合など
            System.out.println("Room " + roomId + " no longer has 4 players, abort sending.");
            return;
        }

        // [1] isDemonフラグを誰か1人だけ true にする (例: 乱数で決定)
        // [2] JSON のリストを作る
        // [3] RestTemplate や WebClient で POST する

        // 例: isDemon をランダムに1名だけ true にする
        List<PlayerInfo> infoList = new ArrayList<>();
        int demonIndex = new Random().nextInt(players.size());
        int idx = 0;
        for (Player p : players) {
            boolean isDemon = (idx == demonIndex);
            infoList.add(new PlayerInfo(p.getUserId(), p.getUserName(), isDemon));
            idx++;
        }

        // POST 用に JSON へシリアライズ
        // Spring なら RestTemplate でも WebClient でも可
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8000/api/init-player-info";
            ResponseEntity<String> response = restTemplate.postForEntity(url, infoList, String.class);
            System.out.println("Sent player info, response: " + response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
