package com.example.myweb.chat.repository;

import com.example.myweb.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoom_RoomId(String roomId);
    ChatMessage findTopByRoom_RoomIdOrderByTimestampDesc(String roomId); 
    Page<ChatMessage> findByRoom_RoomId(String roomId, Pageable pageable); 
    
    List<ChatMessage> findByReceiver_Nickname(String nickname);
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.receiver.nickname = :nickname AND m.isRead = false")
    int countUnreadMessagesByReceiverNickname(@Param("nickname") String nickname);
}
