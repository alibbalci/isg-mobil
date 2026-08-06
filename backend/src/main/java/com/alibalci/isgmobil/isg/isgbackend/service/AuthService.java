package com.alibalci.isgmobil.isg.isgbackend.service;

import org.springframework.stereotype.Service;

import com.alibalci.isgmobil.isg.isgbackend.dto.AuthResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.LoginRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.RegisterRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.UserResponse;

@Service
public interface AuthService {
    UserResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest request);
}
