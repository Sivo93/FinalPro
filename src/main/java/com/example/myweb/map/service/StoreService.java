package com.example.myweb.map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myweb.map.entity.Store;
import com.example.myweb.map.repository.StoreRepository;



@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public void addStore(Store store) {
        storeRepository.save(store);
    }

    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
    }
}
