package com.example.myweb.shop;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class ShopEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nom;

    private String productname; // 상품이름
    private String description; 
    private double price; // 가격
    private String sellernickname; // 판매자 닉네임
    
    @ElementCollection
    @CollectionTable(name="shop_images", joinColumns=@JoinColumn(name="nom"))
    @Column(name="image_url")
    private List<String> imageUrls=new ArrayList<>();
    
    
}
