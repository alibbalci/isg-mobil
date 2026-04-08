package com.alibalci.isgmobil.isg.isgbackend.service;


import com.alibalci.isgmobil.isg.isgbackend.dto.LoginRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.RegisterRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.UserResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    UserResponse register(RegisterRequest registerRequest);
    String login(LoginRequest request);
}
