package com.example.myweb.chat.dto;

import com.example.myweb.chat.entity.ChatMessage;
import com.example.myweb.user.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatMessageDTO {
    private Long id;
    private String content;
    private LocalDateTime timestamp;  // LocalDateTime으로 변경
    private UserDTO sender;
    private UserDTO receiver;
    private String fileName;
    private String fileUrl;
    private boolean isRead;

    // 엔티티를 DTO로 변환
    public static ChatMessageDTO toChatMessageDTO(ChatMessage chatMessage) {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setId(chatMessage.getId());
        chatMessageDTO.setContent(chatMessage.getContent());
        chatMessageDTO.setTimestamp(chatMessage.getTimestamp());  // LocalDateTime으로 설정
        chatMessageDTO.setSender(UserDTO.toUserDTO(chatMessage.getSender()));
        chatMessageDTO.setReceiver(UserDTO.toUserDTO(chatMessage.getReceiver()));
        chatMessageDTO.setFileName(chatMessage.getFileName());
        chatMessageDTO.setFileUrl(chatMessage.getFileUrl());
        chatMessageDTO.setRead(chatMessage.isRead());

        return chatMessageDTO;
    }
}

