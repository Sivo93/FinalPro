package com.example.myweb.chat.controller;

import com.example.myweb.chat.dto.ChatMessageDTO;
import com.example.myweb.chat.dto.ChatRoomDTO;
import com.example.myweb.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 특정 채팅방의 모든 메시지 불러오는 메서드
    @GetMapping("/allMessages")
    @ResponseBody
    public List<ChatMessageDTO> getAllMessages(@RequestParam("roomId") String roomId) {
        return chatService.getAllMessages(roomId);
    }

    // 현재 사용자와 연결된 모든 채팅방 목록 불러오는 메서드
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDTO> getChatRooms(HttpSession session) {
        String nickname = (String) session.getAttribute("nickname");
        if (nickname == null) {
            System.out.println("Nickname is missing in the session.");
            throw new IllegalArgumentException("Nickname parameter is required");
        }
        return chatService.getChatRooms(nickname);
    }


    // 메시지 처리
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/{roomId}")
    public void sendMessage(@Payload ChatMessageDTO chatMessage) {
        ChatMessageDTO savedMessage = chatService.saveMessage(chatMessage); // 메시지 저장
        String roomId = chatService.generateRoomId(chatMessage.getSender().getNickname(), chatMessage.getReceiver().getNickname()); // 방 생성
        messagingTemplate.convertAndSend("/topic/" + roomId, savedMessage);

        // 알림 전송
        messagingTemplate.convertAndSend("/topic/notifications/" + chatMessage.getReceiver().getNickname(), savedMessage);

        // 메시지 리스트 갱신
        messagingTemplate.convertAndSend("/topic/chat-list/" + chatMessage.getSender().getNickname(), chatService.getChatRooms(chatMessage.getSender().getNickname()));
        messagingTemplate.convertAndSend("/topic/chat-list/" + chatMessage.getReceiver().getNickname(), chatService.getChatRooms(chatMessage.getReceiver().getNickname()));
    }

    // 메시지 읽음 상태 표시
    @PostMapping("/messages/markAsRead")
    @ResponseBody
    public void markMessageAsRead(@RequestParam("messageId") Long messageId) {
        chatService.markMessageAsRead(messageId);
    }

    // 발신자 수신자 간의 모든 메시지 읽음 상태 표시
    @PostMapping("/messages/markAllAsRead")
    @ResponseBody
    public void markAllMessagesAsRead(@RequestParam("sender") String sender, @RequestParam("receiver") String receiver) {
        chatService.markAllMessagesAsRead(sender, receiver);
    }
    
    // 읽지 않은 메시지 목록을 반환하는 엔드포인트
    @GetMapping("/unreadMessages")
    @ResponseBody
    public List<ChatMessageDTO> getUnreadMessages(HttpSession session) {
        String nickname = (String) session.getAttribute("nickname");
        if (nickname == null) {
            throw new IllegalArgumentException("Nickname is required");
        }
        return chatService.getUnreadMessages(nickname);
    }

    @GetMapping("/userRooms")
    @ResponseBody
    public List<ChatRoomDTO> getUserChatRooms(HttpSession session) {
        String nickname = (String) session.getAttribute("nickname");
        if (nickname == null) {
            throw new IllegalArgumentException("Nickname parameter is required");
        }
        return chatService.getChatRooms(nickname);
    }
}
