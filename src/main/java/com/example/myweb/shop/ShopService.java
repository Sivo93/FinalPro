package com.example.myweb.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    // 모든 상품을 가져옵니다.
    public List<ShopEntity> getAllShops() {
        return shopRepository.findAll();
    }

    // 특정 상품을 ID로 가져옵니다.
    public Optional<ShopEntity> getShopById(Long nom) {
        return shopRepository.findById(nom);
    }

    // 새로운 상품을 저장합니다.
    public ShopEntity saveShop(ShopEntity shop) {
        return shopRepository.save(shop);
    }

    // 키워드를 사용하여 상품을 검색합니다.
    public List<ShopEntity> searchShops(String keyword) {
        return shopRepository.findByProductnameContainingOrDescriptionContaining(keyword, keyword);
    }

    // 상품 삭제합니다.
    public void deleteShopById(Long nom) {
        shopRepository.deleteById(nom);
    }
}
