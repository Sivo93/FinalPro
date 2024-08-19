package com.example.myweb.map.controller;

import com.example.myweb.map.entity.Store;
import com.example.myweb.map.service.StoreService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    @Value("${store.image.upload-dir}")
    private String uploadDir;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<Void> addStore(@RequestParam("name") String name,
                                          @RequestParam("address") String address,
                                          @RequestParam("latitude") double latitude,
                                          @RequestParam("longitude") double longitude,
                                          @RequestParam("phoneNumber") String phoneNumber,
                                          @RequestParam("description") String description,
                                          @RequestParam(value = "reviews", required = false) String reviews,
                                          @RequestParam("photo") MultipartFile photo,
                                          @RequestParam("tags") String tags) throws IOException {
        // 파일 업로드 처리
        String uploadDir = new ClassPathResource("static/uploads/storeimg/").getFile().getAbsolutePath();
        String filename = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
        Path path = Paths.get(uploadDir, filename);

        Files.createDirectories(path.getParent());
        if (!photo.isEmpty()) {
            Files.write(path, photo.getBytes());
        } else {
            throw new IOException("파일이 비어 있습니다.");
        }
        String photoUrl = "/uploads/storeimg/" + filename;

        // 태그 문자열을 Set으로 변환
        Set<String> tagSet = new HashSet<>();
        if (tags != null && !tags.trim().isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> tagList = objectMapper.readValue(tags, new TypeReference<List<String>>() {});
                tagSet.addAll(tagList);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("태그 형식이 잘못되었습니다.");
            }
        }
        // 리뷰 문자열을 List로 변환
        List<String> reviewList = new ArrayList<>();
        if (reviews != null && !reviews.trim().isEmpty()) {
            reviewList.add(reviews); // 단일 리뷰로 처리 (추후 여러 리뷰 추가 가능)
        }

        // Store 객체 생성 및 저장
        Store store = new Store(name, address, latitude, longitude, phoneNumber, description, photoUrl, tagSet, reviewList);
        storeService.saveStore(store);

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/map")).build();
    }

    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        List<Store> stores = storeService.getAllStores();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getTags() {
        List<Store> stores = storeService.getAllStores();
        Set<String> tagSet = new HashSet<>();

        for (Store store : stores) {
            if (store.getTags() != null) {
                tagSet.addAll(store.getTags());
            }
        }

        List<String> tagList = tagSet.stream().collect(Collectors.toList());
        return ResponseEntity.ok(tagList);
    }
    @PostMapping("/{id}/reviews")
    public ResponseEntity<Map<String, String>> addReview(@PathVariable Long id, @RequestBody String review) {
        Map<String, String> response = new HashMap<>();
        try {
            storeService.addReview(id, review);
            response.put("message", "Review added successfully");
            // 로그를 통해 응답 내용을 확인합니다.
            System.out.println("Response: " + response);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", "Invalid store ID");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "An error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Store> getStore(@PathVariable Long id) {
        Store store = storeService.getStoreById(id);
        return store != null ? ResponseEntity.ok(store) : ResponseEntity.notFound().build();
    }
}
