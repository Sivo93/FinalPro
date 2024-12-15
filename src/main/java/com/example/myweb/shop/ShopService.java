package com.example.myweb.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

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
    public Page<ShopEntity> searchShops(String keyword, int page, int size) {
        return shopRepository.findByProductnameContainingOrDescriptionContaining(keyword, keyword, PageRequest.of(page, size));
    }

    // 판매자별로 상품 검색
    public Page<ShopEntity> searchBySeller(String sellerNickname, int page, int size) {
        return shopRepository.findBySellernicknameContaining(sellerNickname, PageRequest.of(page, size));
    }

    // 상품명으로 검색
    public Page<ShopEntity> searchByProductName(String productName, int page, int size) {
        return shopRepository.findByProductnameContaining(productName, PageRequest.of(page, size));
    }

    // 상품 삭제합니다.
    public void deleteShopById(Long nom) {
        shopRepository.deleteById(nom);
    }

    // 모든 상품을 페이징 처리하여 가져옵니다.
    public Page<ShopEntity> getAllShops(int page, int size) {
        return shopRepository.findAll(PageRequest.of(page, size));
    }

    // 판매자가 올린 다른 상품을 조회합니다. 현재 상품은 제외.
    public List<ShopEntity> getOtherShopsBySeller(String sellerNickname, Long excludeNom) {
        return shopRepository.findBySellernicknameAndNomNot(sellerNickname, excludeNom);
    }
}
