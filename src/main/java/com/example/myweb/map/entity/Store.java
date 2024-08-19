package com.example.myweb.map.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import java.util.Set;

@Data
@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String phoneNumber;
    private String description;
    private String reviews;
    private String photoUrl;

    @ElementCollection
    @CollectionTable(name = "store_tags", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "tag")
    private Set<String> tags; // 태그 필드를 Set<String>으로 변경

    // 기본 생성자
    public Store() {
    }

    // 모든 필드를 포함한 생성자
    public Store(String name, String address, double latitude, double longitude, String phoneNumber, String description, String reviews, String photoUrl, Set<String> tags) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.reviews = reviews;
        this.photoUrl = photoUrl;
        this.tags = tags;
    }
    
}
