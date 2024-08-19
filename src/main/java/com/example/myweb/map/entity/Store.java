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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private String photoUrl;

    @ElementCollection
    @CollectionTable(name = "store_tags", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "store_reviews", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "review")
    private List<String> reviews = new ArrayList<>();

    // 기본 생성자
    public Store() {}

    // 모든 필드를 포함한 생성자
    public Store(String name, String address, double latitude, double longitude, String phoneNumber, String description, String photoUrl, Set<String> tags, List<String> reviews) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photoUrl = photoUrl;
        this.tags = tags;
        this.reviews = reviews;
    }
}
