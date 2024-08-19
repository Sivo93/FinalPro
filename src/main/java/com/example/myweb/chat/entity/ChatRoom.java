package com.example.myweb.chat.entity;

import com.example.myweb.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sender_id", referencedColumnName = "seq")
    private UserEntity sender;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "receiver_id", referencedColumnName = "seq")
    private UserEntity receiver;

    @JsonIgnore
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ChatMessage> messages = new HashSet<>();

    private LocalDateTime senderLeaveTime; // 발신자가 나간 시점
    private LocalDateTime receiverLeaveTime; // 수신자가 나간 시점

    public ChatRoom() {}

    public ChatRoom(String roomId, UserEntity sender, UserEntity receiver) {
        this.roomId = roomId;
        this.sender = sender;
        this.receiver = receiver;
    }

    public void leaveRoom(String nickname) {
        if (sender.getNickname().equals(nickname)) {
            this.senderLeaveTime = LocalDateTime.now();
        } else if (receiver.getNickname().equals(nickname)) {
            this.receiverLeaveTime = LocalDateTime.now();
        }
    }

    public LocalDateTime getLeaveTime(String nickname) {
        if (sender.getNickname().equals(nickname)) {
            return senderLeaveTime;
        } else if (receiver.getNickname().equals(nickname)) {
            return receiverLeaveTime;
        }
        return null;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", roomId='" + roomId + '\'' +
                ", sender=" + sender.getNickname() +
                ", receiver=" + receiver.getNickname() +
                '}';
    }
}