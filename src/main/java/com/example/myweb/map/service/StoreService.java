package com.example.myweb.map.service;

import com.example.myweb.map.entity.Store;
import com.example.myweb.map.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
