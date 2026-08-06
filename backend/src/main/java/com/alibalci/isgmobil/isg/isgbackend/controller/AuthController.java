package com.alibalci.isgmobil.isg.isgbackend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibalci.isgmobil.isg.isgbackend.dto.AuthResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.LoginRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.RegisterRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.UserResponse;
import com.alibalci.isgmobil.isg.isgbackend.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
