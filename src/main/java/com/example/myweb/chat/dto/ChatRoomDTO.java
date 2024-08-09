package com.example.myweb.chat.dto;

import com.example.myweb.chat.entity.ChatRoom;
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
public class ChatRoomDTO {
    private Long id;
    private String roomId;
    private UserDTO sender;
    private UserDTO receiver;
    private String latestMessage;
    private String latestMessageTime;
    
    
    // 엔티티와 최신 메시지 정보를 기반으로 dto객체 생성
    public ChatRoomDTO(ChatRoom chatRoom, String latestMessage, String latestMessageTime) {
        this.id = chatRoom.getId();
        this.roomId = chatRoom.getRoomId();
        this.sender = UserDTO.toUserDTO(chatRoom.getSender());
        this.receiver = UserDTO.toUserDTO(chatRoom.getReceiver());
        this.latestMessage = latestMessage;
        this.latestMessageTime = latestMessageTime;
    }
    
    // 엔티티를 dto로 변환(정적)
    public static ChatRoomDTO toChatRoomDTO(ChatRoom chatRoom) {
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setId(chatRoom.getId());
        chatRoomDTO.setRoomId(chatRoom.getRoomId());
        chatRoomDTO.setSender(UserDTO.toUserDTO(chatRoom.getSender()));
        chatRoomDTO.setReceiver(UserDTO.toUserDTO(chatRoom.getReceiver()));
        chatRoomDTO.setLatestMessage("최근 메시지 없음");
        chatRoomDTO.setLatestMessageTime(null);

        return chatRoomDTO;
    }
}
