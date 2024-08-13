package com.example.myweb.chat.repository;

import com.example.myweb.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomId(String roomId);

    // 사용자의 username을 기준으로 사용자와 연결된 모든 채팅방 조회
    @Query("SELECT c FROM ChatRoom c WHERE c.sender.nickname = :username OR c.receiver.nickname = :username")
    List<ChatRoom> findByUsername(@Param("username") String username);
    
    // 특정 사용자의 나간 시간을 가져오는 쿼리
    @Query("SELECT CASE WHEN c.sender.nickname = :nickname THEN c.senderLeaveTime ELSE c.receiverLeaveTime END FROM ChatRoom c WHERE c.roomId = :roomId")
    LocalDateTime findLeaveTimeByRoomIdAndNickname(@Param("roomId") String roomId, @Param("nickname") String nickname);

}
