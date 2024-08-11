package com.example.myweb.chat.entity;

import com.example.myweb.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "seq")
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "seq")
    private UserEntity receiver;

    @JsonIgnore
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ChatMessage> messages = new HashSet<>();

    public ChatRoom() {}

    public ChatRoom(String roomId, UserEntity sender, UserEntity receiver) {
        this.roomId = roomId;
        this.sender = sender;
        this.receiver = receiver;
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
