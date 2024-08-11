package com.example.myweb.chat.entity;

import com.example.myweb.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private String timestamp;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private ChatRoom room;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "seq")
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "seq")
    private UserEntity receiver;

    private String fileName;
    private String fileUrl;

    private boolean isRead = false;  // 읽음 상태 필드 추가
}
