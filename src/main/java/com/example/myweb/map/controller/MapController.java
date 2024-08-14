package com.example.myweb.map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.myweb.map.entity.Store;
import com.example.myweb.map.repository.StoreRepository;
import com.google.gson.Gson;


@Controller
public class MapController {

    @Autowired
    private StoreRepository storeRepository;

    @GetMapping("/map")
    public String showMap(Model model) {
        List<Store> stores = storeRepository.findAll();
        
        // 데이터를 로그로 출력해 확인
        System.out.println("Stores from DB: " + stores);

        // Gson을 사용해 stores 객체를 JSON 문자열로 변환
        Gson gson = new Gson();
        String storesJson = gson.toJson(stores);

        // JSON 문자열을 로그로 출력해 확인
        System.out.println("Stores JSON: " + storesJson);

        // JSON 문자열을 모델에 추가
        model.addAttribute("storesJson", storesJson);
        return "map"; // 템플릿 파일 이름
    }
}
