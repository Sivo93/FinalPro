package com.example.myweb.chat.repository;

import com.example.myweb.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomId(String roomId);

    // 사용자의 username을 기준으로 사용자와 연결된 모든 채팅방 조회
    @Query("SELECT c FROM ChatRoom c WHERE c.sender.nickname = :username OR c.receiver.nickname = :username")
    List<ChatRoom> findByUsername(@Param("username") String username);
}
