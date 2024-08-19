package com.example.myweb.shop;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShopRepository extends JpaRepository<ShopEntity, Long> {

    // 상품명 또는 설명으로 검색하는 메서드
    List<ShopEntity> findByProductnameContainingOrDescriptionContaining(String productname, String description);

    // 페이징기능 추가
    Page<ShopEntity> findByProductnameContainingOrDescriptionContaining(String productname, String description, Pageable pageable);

    // 판매자 닉네임으로 상품 검색 (페이징 처리)
    Page<ShopEntity> findBySellernicknameContaining(String sellernickname, Pageable pageable);

    // 상품명으로 검색 (페이징 처리)
    Page<ShopEntity> findByProductnameContaining(String productname, Pageable pageable);
    
    // 판매자가 올린 다른 상품을 조회 (현재 상품 제외)
    List<ShopEntity> findBySellernicknameAndNomNot(String sellernickname, Long nom);

}
