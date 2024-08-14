package com.example.myweb.chat.controller;

import com.example.myweb.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/getUnreadCount")
    public void getUnreadCount(@Payload String payload) {
        String nickname = parseNickname(payload); // nickname 추출
        int unreadCount = chatService.getUnreadMessageCount(nickname);
        messagingTemplate.convertAndSend("/topic/unread-count/" + nickname, new UnreadCountResponse(unreadCount));
    }

    private String parseNickname(String payload) {
        // JSON 형태로 받은 payload에서 nickname 추출하는 로직 추가
        // 예: {"nickname": "user1"} 형태에서 user1 추출
        // 이 부분은 구현하기 나름입니다. 
        // 간단히 아래와 같은 형식으로 가능:
        return payload.replace("{\"nickname\":\"", "").replace("\"}", "");
    }

    public static class UnreadCountResponse {
        private int unreadCount;

        public UnreadCountResponse(int unreadCount) {
            this.unreadCount = unreadCount;
        }

        public int getUnreadCount() {
            return unreadCount;
        }

        public void setUnreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
        }
    }
}
