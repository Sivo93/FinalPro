package com.example.myweb.map.controller;

<<<<<<< HEAD
public class MapController {

}
=======
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.myweb.map.entity.Store;
import com.example.myweb.map.service.StoreService;

@Controller
public class MapController {

    @Autowired
    private StoreService storeService;

    @GetMapping("/map")
    public String showMap(Model model) {
        List<Store> stores = storeService.getAllStores();
        model.addAttribute("stores", stores);
        return "outside";
    }
}
>>>>>>> ce34d97 (Initial commit)
