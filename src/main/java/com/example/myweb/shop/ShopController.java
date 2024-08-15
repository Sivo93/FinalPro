package com.example.myweb.shop;

import com.example.myweb.shop.ShopEntity;
import com.example.myweb.shop.ShopService;
import com.example.myweb.user.dto.UserDTO;
import com.example.myweb.user.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    // 모든 상품 목록을 가져옵니다. 검색어가 있을 경우 검색 결과를 반환합니다.
    @GetMapping("/shoplist")
    public String getAllShops(@RequestParam(required = false) String keyword, Model model, HttpSession session) {
        List<ShopEntity> shops;
        if (keyword != null) {
            shops = shopService.searchShops(keyword);
        } else {
            shops = shopService.getAllShops();
        }
        model.addAttribute("shops", shops);

        String loginid = (String) session.getAttribute("loginid");
        if (loginid != null) {
            UserDTO userDTO = userService.findByLoginid(loginid);
            model.addAttribute("user", userDTO);
        }

        return "shop/shoplist";
    }

    // 특정 상품의 상세 정보
    @GetMapping("/{nom}")
    public String getShopDetail(@PathVariable Long nom, Model model, HttpSession session) {
        ShopEntity shop = shopService.getShopById(nom)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        model.addAttribute("shop", shop);

        String loginid = (String) session.getAttribute("loginid");
        if (loginid != null) {
            UserDTO userDTO = userService.findByLoginid(loginid);
            model.addAttribute("user", userDTO);
        }

        return "shop/shopdetail";
    }

    // 상품 등록 페이지
    @GetMapping("/addshop")
    public String showAddShopForm(Model model, HttpSession session) {
        String loginid = (String) session.getAttribute("loginid");
        if (loginid != null) {
            UserDTO userDTO = userService.findByLoginid(loginid);
            model.addAttribute("nickname", userDTO.getNickname());
        }

        model.addAttribute("shop", new ShopEntity());
        return "shop/addshop";
    }

    // 새로운 상품을 등록
    @PostMapping("/addshop")
    public String addShop(@ModelAttribute("shop") ShopEntity shop, @RequestParam("images") MultipartFile[] images, HttpSession session) throws IOException {
        // 이미지를 저장하고 이미지 URL을 ShopEntity에 추가
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = saveImage(image);
            imageUrls.add(imageUrl);
        }
        shop.setImageUrls(imageUrls);

        String nickname = (String) session.getAttribute("nickname");
        if (nickname != null) {
            shop.setSellernickname(nickname);
        }

        shopService.saveShop(shop);
        return "redirect:/shop/shoplist";
    }

    // 이미지 저장 로직
    private String saveImage(MultipartFile image) throws IOException {
    	// 폴더 오류나면 해당 폴더 우클릭 후 읽기 전용 체크를 푸세요
        // 프로젝트 루트 디렉토리 경로를 얻습니다.
        String projectDir = System.getProperty("user.dir");
        String uploadDir = projectDir + "/uploads/images";
        String originalFilename = image.getOriginalFilename();
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        String filePath = Paths.get(uploadDir, uniqueFilename).toString();

        
        // 파일 이름에 시간을 추가하여 고유한 파일 이름을 만듭니다.
        // 저장 디렉토리가 없으면 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일 저장
        Files.write(Paths.get(filePath), image.getBytes());

        // 반환 URL 경로
        return "/uploads/images/" + uniqueFilename;
    }
    // 상품 삭제
    @PostMapping("/delete/{nom}")
    public String deleteShop(@PathVariable Long nom) {
        shopService.deleteShopById(nom);
        return "redirect:/shop/shoplist";
    }
}
