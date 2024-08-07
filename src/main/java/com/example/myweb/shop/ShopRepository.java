package com.example.myweb.shop;

import com.example.myweb.shop.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long> {
    List<ShopEntity> findByNameContainingOrDescriptionContaining(String name, String description);
}
