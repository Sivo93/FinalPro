package com.example.myweb.map.service;

import com.example.myweb.map.entity.Store;
import com.example.myweb.map.repository.StoreRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Store saveStore(Store store) {
        return storeRepository.save(store);
    }

    @Transactional
    public void addReview(Long storeId, String review) {
        Optional<Store> optionalStore = storeRepository.findById(storeId);
        if (optionalStore.isPresent()) {
            Store store = optionalStore.get();
            List<String> reviews = store.getReviews();

            if (reviews == null) {
                reviews = new ArrayList<>();
            }

            reviews.add(review); // 새로운 리뷰를 추가

            store.setReviews(reviews);
            storeRepository.save(store); // 리뷰를 업데이트된 리스트로 저장
        } else {
            throw new IllegalArgumentException("Invalid store ID");
        }
    }

    public Store getStoreById(Long id) {
        return storeRepository.findById(id).orElse(null);
    }
}
