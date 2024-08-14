package com.example.myweb.chat.controller;

import com.example.myweb.chat.dto.ChatMessageDTO;
import com.example.myweb.chat.service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getNotifications(HttpSession session) {
        String nickname = (String) session.getAttribute("nickname");

        if (nickname == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // 읽지 않은 메시지 개수
        int unreadCount = chatService.getUnreadMessageCount(nickname);

        // 최근 5개의 메시지를 가져오고, 동일한 발신자가 보낸 메시지를 통합
        List<ChatMessageDTO> recentMessages = chatService.getRecentMessages(nickname);

        Map<String, Object> response = new HashMap<>();
        response.put("unreadCount", unreadCount);
        response.put("notifications", recentMessages);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/unread-messages-count")
    public ResponseEntity<Integer> getUnreadMessagesCount(@RequestParam String nickname) {
        int unreadCount = chatService.getUnreadMessageCount(nickname);
        return ResponseEntity.ok(unreadCount);
    }
}
