package com.example.myweb.chat.dto;

import com.example.myweb.chat.entity.ChatMessage;
import com.example.myweb.user.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatMessageDTO {
    private Long id;
    private String content;
    private String timestamp;
    private UserDTO sender;
    private UserDTO receiver;
    private String fileName;
    private String fileUrl;
    private boolean isRead;  // 읽음 상태 필드 추가

    // 엔티티를 dto로 변환
    public static ChatMessageDTO toChatMessageDTO(ChatMessage chatMessage) {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setId(chatMessage.getId());
        chatMessageDTO.setContent(chatMessage.getContent());
        chatMessageDTO.setTimestamp(chatMessage.getTimestamp());
        chatMessageDTO.setSender(UserDTO.toUserDTO(chatMessage.getSender()));
        chatMessageDTO.setReceiver(UserDTO.toUserDTO(chatMessage.getReceiver()));
        chatMessageDTO.setFileName(chatMessage.getFileName());
        chatMessageDTO.setFileUrl(chatMessage.getFileUrl());
        chatMessageDTO.setRead(chatMessage.isRead());  // 읽음 상태 설정

        return chatMessageDTO;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
}
