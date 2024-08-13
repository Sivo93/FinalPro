package com.example.myweb.chat.repository;

import com.example.myweb.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoom_RoomId(String roomId);
    ChatMessage findTopByRoom_RoomIdOrderByTimestampDesc(String roomId); 

    int countByReceiver_NicknameAndIsReadFalse(String nickname);
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.receiver.nickname = :nickname AND m.isRead = false")
    int countUnreadMessagesByReceiverNickname(@Param("nickname") String nickname);
    List<ChatMessage> findByReceiver_NicknameAndIsReadFalse(String nickname);

    // 특정 수신자에 대해 타임스탬프를 기준으로 가장 최근의 5개의 메시지를 가져오는 메서드
    List<ChatMessage> findTop5ByReceiver_NicknameOrderByTimestampDesc(String receiverNickname);

    // 사용자가 채팅방을 나간 이후의 메시지만 가져오는 메서드
    List<ChatMessage> findByRoom_RoomIdAndTimestampAfter(String roomId, LocalDateTime timestamp);


}
