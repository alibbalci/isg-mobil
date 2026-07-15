package com.alibalci.isgmobil.isg.isgbackend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public Map<String, String> test() {
        return Map.of("message", "Backend bağlantısı başarılı");
    }
}