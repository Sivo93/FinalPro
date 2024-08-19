package com.example.myweb.shop;

import com.example.myweb.shop.ShopEntity;
import com.example.myweb.shop.ShopService;
import com.example.myweb.user.dto.UserDTO;
import com.example.myweb.user.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.apache.poi.ss.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/shop")
public class ShopController implements WebMvcConfigurer { // WebMvcConfigurer를 구현하여 정적 리소스 경로를 설정합니다.

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    // 로그인 아이디를 쉽게 가져오기 위한 메서드로 중복된 코드를 제거합니다.
    private String getLoginId(HttpSession session) {
        return (String) session.getAttribute("loginid");
    }

    // 특정 상품의 상세 정보
    @GetMapping("/{nom}")
    public String getShopDetail(@PathVariable Long nom, Model model, HttpSession session) {
        ShopEntity shop = shopService.getShopById(nom)
                .orElseThrow(() -> new ShopNotFoundException("Shop not found")); // 커스텀 예외를 사용하여 예외 처리

        List<ShopEntity> otherShops = shopService.getOtherShopsBySeller(shop.getSellernickname(), nom);
        model.addAttribute("shop", shop);
        model.addAttribute("otherShops", otherShops);

        String loginid = getLoginId(session); // 중복된 코드 제거 후 재사용
        if (loginid != null) {
            UserDTO userDTO = userService.findByLoginid(loginid);
            model.addAttribute("user", userDTO);
        }

        return "shop/shopdetail";
    }

    // 상품 등록 페이지
    @GetMapping("/addshop")
    public String showAddShopForm(Model model, HttpSession session) {
        String loginid = getLoginId(session);
        if (loginid != null) {
            UserDTO userDTO = userService.findByLoginid(loginid);
            model.addAttribute("nickname", userDTO.getNickname());
        }

        model.addAttribute("shop", new ShopEntity());
        return "shop/addshop";
    }

    // 새로운 상품을 등록
    @PostMapping("/addshop")
	public String addShop(@ModelAttribute("shop") ShopEntity shop, 
    						@RequestParam("images") MultipartFile[] images, 
    						HttpSession session) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                String imageUrl = saveImage(image);
                imageUrls.add(imageUrl);
            }
        }

        shop.setImageUrls(imageUrls.isEmpty() ? null : imageUrls); 

        String nickname = (String) session.getAttribute("nickname");
        if (nickname != null) {
            shop.setSellernickname(nickname);
        }

        shopService.saveShop(shop);
        return "redirect:/shop/shoplist";
    }

    // 이미지 저장 로직
    private String saveImage(MultipartFile image) throws IOException {
        String uploadDir = System.getProperty("user.dir") + "/uploads/images";
        String originalFilename = image.getOriginalFilename();
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        String filePath = Paths.get(uploadDir, uniqueFilename).toString();

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Files.write(Paths.get(filePath), image.getBytes());

        return "/uploads/images/" + uniqueFilename;
    }

    // 정적 리소스 경로를 설정합니다.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	String uploadDir = System.getProperty("user.dir") + "/uploads/images/";
    	registry.addResourceHandler("/uploads/images/**")
        		.addResourceLocations("file:" + uploadDir);
    }

    // 상품 삭제
    @PostMapping("/delete/{nom}")
    public String deleteShop(@PathVariable Long nom) {
        shopService.deleteShopById(nom);
        return "redirect:/shop/shoplist";
    }

    // 페이징된 모든 상품 목록을 가져옵니다. 검색어가 있을 경우 검색 결과에 따른 목록 출력
    @GetMapping("/shoplist")
    public String getAllShops(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false, defaultValue = "product") String searchType,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model, HttpSession session) {

        Page<ShopEntity> shopPage;

        if (keyword != null && !keyword.isEmpty()) {
            if ("seller".equals(searchType)) {
                shopPage = shopService.searchBySeller(keyword, page - 1, size);
            } else {
                shopPage = shopService.searchByProductName(keyword, page - 1, size);
            }
            model.addAttribute("keyword", keyword);
            model.addAttribute("searchType", searchType);
            model.addAttribute("noResults", shopPage.isEmpty());
        } else {
            shopPage = shopService.getAllShops(page - 1, size);
            model.addAttribute("noResults", false);
        }

        model.addAttribute("shops", shopPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", shopPage.getTotalPages());

        String loginid = getLoginId(session);
        if (loginid != null) {
            UserDTO userDTO = userService.findByLoginid(loginid);
            model.addAttribute("user", userDTO);
        }

        return "shop/shoplist";
    }

    // 상품 정보 수정 페이지
    @GetMapping("/edit/{nom}")
    public String editShop(@PathVariable("nom") Long nom, Model model) {
        Optional<ShopEntity> optionalShop = shopService.getShopById(nom);
        if (optionalShop.isPresent()) {
            model.addAttribute("shop", optionalShop.get());
            return "shop/editshop";
        } else {
            throw new ShopNotFoundException("Shop not found"); // 커스텀 예외를 던짐
        }
    }

    // 상품 업데이트 처리
    @PostMapping("/update/{nom}")
    public String updateShop(@PathVariable("nom") Long nom, 
    		@RequestParam("productname") String productName, 
    		@RequestParam("description") String description, 
    		@RequestParam("price") double price, 
    		@RequestParam(value = "images", required = false) MultipartFile[] images) throws IOException {
    	
    	Optional<ShopEntity> optionalShop = shopService.getShopById(nom);
    	if (optionalShop.isPresent()) {
    		ShopEntity shop = optionalShop.get();
    		shop.setProductname(productName);
    		shop.setDescription(description);
    		shop.setPrice(price);
    		
    		List<String> imageUrls = new ArrayList<>();
    		if(images != null) {
    			for(MultipartFile image : images) {
    				if(!image.isEmpty()) {
    					String imageUrl = saveImage(image);
    					imageUrls.add(imageUrl);
    				}
    			}
    		}
    		shop.setImageUrls(imageUrls.isEmpty() ? shop.getImageUrls() : imageUrls);

    		shopService.saveShop(shop);
            return "redirect:/shop/" + nom; // 리다이렉션 경로 수정
        } else {
        	throw new ShopNotFoundException("Shop not found");
        }
    }

    // 커스텀 예외 정의
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ShopNotFoundException extends RuntimeException {
        public ShopNotFoundException(String message) {
            super(message);
        }
    }
}
