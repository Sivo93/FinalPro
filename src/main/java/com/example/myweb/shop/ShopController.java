package com.example.myweb.shop;

import com.example.myweb.shop.ShopEntity;
import com.example.myweb.shop.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    // 모든 상품 목록을 가져옵니다. 검색어가 있을 경우 검색 결과를 반환합니다.
    @GetMapping("/shoplist")
    public String getAllShops(@RequestParam(required = false) String keyword, Model model) {
        List<ShopEntity> shops;
        if (keyword != null) {
            shops = shopService.searchShops(keyword);
        } else {
            shops = shopService.getAllShops();
        }
        model.addAttribute("shops", shops);
        model.addAttribute("keyword", keyword);
        return "shop/shoplist";
    }

    // 특정 상품의 상세 정보
    @GetMapping("/{nom}")
    public String getShopDetail(@PathVariable Long nom, Model model) {
        ShopEntity shop = shopService.getShopById(nom)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        model.addAttribute("shop", shop);
        return "shop/shopdetail";
    }

    // 상품 등록 페이지
    @GetMapping("/addshop")
    public String showAddShopForm(Model model) {
        model.addAttribute("shop", new ShopEntity());
        return "shop/addshop";
    }

    // 새로운 상품을 등록
    @PostMapping("/addshop")
    public String addShop(@ModelAttribute("shop") ShopEntity shop, @RequestParam("images") MultipartFile[] images) throws IOException {
    	
    	//이미지를 저장하고 이미지url을 shopentity에 추가
    	List<String> imageUrls=new ArrayList<>();
    	for(MultipartFile image: images) {
    		//이미지를 저장하고 결로와 url을 얻어와서 imageUrls에 추가
    		String imageUrl=saveImage(image);	//이미지 저장로직
    		imageUrls.add(imageUrl);
    		
    	}
    	//엔티티에 이미지 url리스트설정
    	shop.setImageUrls(imageUrls);
    	//상품 저장
        shopService.saveShop(shop);
        return "redirect:/shop/shoplist";
    }
    
    //이미지 저장 로직
    private String saveImage(MultipartFile image)throws IOException{
    	
    	//로직 구현
    	String uploadDir="uploads/images";
    	String originalFilename=image.getOriginalFilename();
    	String filePath=Paths.get(uploadDir, originalFilename).toString();
    	
    	//저장 디렉토리가 없으면 생성
    	File dir=new File(uploadDir);
    	if(!dir.exists()) {
    		dir.mkdirs();
    		
    	}
    	
    	//파일저장
    	Files.write(Paths.get(filePath), image.getBytes());
    	
    	//반환 url경로
    	return "/images/"+originalFilename;
    	
    }
    
    //상품 삭제
    @PostMapping("/delete/{nom}")
    public String deleteShop(@PathVariable Long nom) {
    	shopService.deleteShopById(nom);
    	return "redirect:/shop/shoplist";
    	
    }
}
