package com.example.myweb.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShopRepository extends JpaRepository<ShopEntity, Long> {
    List<ShopEntity> findByProductnameContainingOrDescriptionContaining(String productname, String description);
}
