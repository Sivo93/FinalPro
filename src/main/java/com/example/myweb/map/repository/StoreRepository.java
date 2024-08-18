package com.example.myweb.map.repository;

import com.example.myweb.map.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
    // 필요한 쿼리 메서드를 추가할 수 있습니다.
}
