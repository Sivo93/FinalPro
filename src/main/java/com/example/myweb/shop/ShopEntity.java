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

@Entity
public class ShopEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nom;

    private String name;
    private String description;
    private double price;
    
    @ElementCollection
    @CollectionTable(name="shop_images", joinColumns=@JoinColumn(name="nom"))
    @Column(name="image_url")
    private List<String> imageUrls=new ArrayList<>();
    
    // getters and setters
    public Long getNom() {
        return nom;
    }

    public void setNom(Long nom) {
        this.nom = nom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}
    
}
