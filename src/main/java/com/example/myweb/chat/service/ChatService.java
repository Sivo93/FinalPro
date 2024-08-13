package com.example.myweb.chat.service;

import com.example.myweb.chat.dto.ChatMessageDTO;
import com.example.myweb.chat.dto.ChatRoomDTO;
import com.example.myweb.chat.entity.ChatMessage;
import com.example.myweb.chat.entity.ChatRoom;
import com.example.myweb.chat.repository.ChatMessageRepository;
import com.example.myweb.chat.repository.ChatRoomRepository;
import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;
    


    // 특정 채팅방의 모든 메시지 호출
    public List<ChatMessageDTO> getAllMessages(String roomId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoom_RoomId(roomId);
        return chatMessages.stream().map(ChatMessageDTO::toChatMessageDTO).collect(Collectors.toList());
    }

    // 특정 사용자와 연결된 모든 채팅방 호출
    public List<ChatRoomDTO> getChatRooms(String username) {
        return chatRoomRepository.findByUsername(username)
                .stream()
                .map(chatRoom -> {
                    // 채팅방의 최신 메시지 호출
                    ChatMessage latestMessage = chatMessageRepository.findTopByRoom_RoomIdOrderByTimestampDesc(chatRoom.getRoomId());
                    // 최신 메시지와 메시지 시간 설정
                    String latestMessageContent = latestMessage != null ? latestMessage.getContent() : "최근 메시지 없음";
                    LocalDateTime latestMessageTime = latestMessage != null ? latestMessage.getTimestamp() : null;
                    
                    return new ChatRoomDTO(chatRoom, latestMessageContent, latestMessageTime);
                })
                .collect(Collectors.toList());
    }
    
    // 발신자와 수진자의 닉네임을 사용해 채팅방 id 생성
    public String generateRoomId(String sender, String receiver) {
        return sender.compareTo(receiver) > 0 ? sender + "_" + receiver : receiver + "_" + sender;
    }
    
    // 메세지 저장
    public ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO) {
        // 발신자와 수신자 정보 가져오기
        UserEntity sender = userRepository.findByNickname(chatMessageDTO.getSender().getNickname())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender nickname: " + chatMessageDTO.getSender().getNickname()));
        UserEntity receiver = userRepository.findByNickname(chatMessageDTO.getReceiver().getNickname())
                .orElseThrow(() -> new IllegalArgumentException("Invalid receiver nickname: " + chatMessageDTO.getReceiver().getNickname()));
        
        // 채팅방을 찾거나 존재하지 않으면 새로 생성
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(generateRoomId(sender.getNickname(), receiver.getNickname()))
                .orElseGet(() -> createChatRoom(sender, receiver));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(chatMessageDTO.getContent());
        chatMessage.setTimestamp(LocalDateTime.now());  // 현재 시간을 설정
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setRoom(chatRoom);
        chatMessage.setFileName(chatMessageDTO.getFileName());
        chatMessage.setFileUrl(chatMessageDTO.getFileUrl());
        chatMessage.setRead(false);

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        return ChatMessageDTO.toChatMessageDTO(savedMessage);
    }


    // 특정 메시지를 읽음으로 표시하는 메서드
    public void markMessageAsRead(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid message ID: " + messageId));
        message.setRead(true);
        chatMessageRepository.save(message);
    }

    // 특정 발신자와 수신자 정보를 가져옴
    public void markAllMessagesAsRead(String senderNickname, String receiverNickname) {
        UserEntity sender = userRepository.findByNickname(senderNickname)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender nickname: " + senderNickname));
        UserEntity receiver = userRepository.findByNickname(receiverNickname)
                .orElseThrow(() -> new IllegalArgumentException("Invalid receiver nickname: " + receiverNickname));
         
        
        // 두 사용자가 속한 채팅방의 모든 메시지를 가져옴
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoom_RoomId(generateRoomId(sender.getNickname(), receiver.getNickname()));
        for (ChatMessage chatMessage : chatMessages) {
            if (chatMessage.getReceiver().equals(receiver)) {
                chatMessage.setRead(true);
            }
        }
        chatMessageRepository.saveAll(chatMessages);
    }

    // 채팅방 생성
    private ChatRoom createChatRoom(UserEntity sender, UserEntity receiver) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomId(generateRoomId(sender.getNickname(), receiver.getNickname()));
        chatRoom.setSender(sender);
        chatRoom.setReceiver(receiver);
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        
        // 새로운 채팅방이 생성되면 실시간으로 업데이트
        messagingTemplate.convertAndSend("/topic/chat-room-updates", new ChatRoomDTO(savedRoom, null, null));
        
        return savedRoom;
    }
    
    // 읽지 않은 메시지 개수를 반환하는 메서드
    public int getUnreadMessageCount(String nickname) {
        return chatMessageRepository.countUnreadMessagesByReceiverNickname(nickname);
    }
    @Autowired
    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }


    // 특정 수신자의 닉네임을 기준으로 최근 5개의 메시지를 가져오고, 동일한 발신자의 메시지를 통합
    public List<ChatMessageDTO> getRecentMessages(String nickname) {
        List<ChatMessage> messages = chatMessageRepository.findTop5ByReceiver_NicknameOrderByTimestampDesc(nickname);

        Map<String, ChatMessageDTO> notificationMap = new LinkedHashMap<>();
        for (ChatMessage message : messages) {
            String sender = message.getSender().getNickname();
            if (notificationMap.containsKey(sender)) {
                notificationMap.get(sender).setContent(notificationMap.get(sender).getContent() + " (다른 메시지 있음)");
            } else {
                notificationMap.put(sender, ChatMessageDTO.toChatMessageDTO(message));
            }
        }

        return new ArrayList<>(notificationMap.values());
    }
    
    
    

}
