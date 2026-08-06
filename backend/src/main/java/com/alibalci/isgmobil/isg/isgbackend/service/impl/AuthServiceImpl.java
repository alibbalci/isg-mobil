package com.alibalci.isgmobil.isg.isgbackend.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alibalci.isgmobil.isg.isgbackend.dto.AuthResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.LoginRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.RegisterRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.UserResponse;
import com.alibalci.isgmobil.isg.isgbackend.entity.Role;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import com.alibalci.isgmobil.isg.isgbackend.exception.ConflictException;
import com.alibalci.isgmobil.isg.isgbackend.exception.UnauthorizedException;
import com.alibalci.isgmobil.isg.isgbackend.repository.UserRepository;
import com.alibalci.isgmobil.isg.isgbackend.security.JwtService;
import com.alibalci.isgmobil.isg.isgbackend.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserResponse register(RegisterRequest registerRequest) {
        // e-mail var mı kontrolü
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new ConflictException(
                    "EMAIL_ALREADY_EXISTS",
                    "Bu e-posta adresi zaten kullanılıyor");
        }
        // yeni kullanıcı oluşumu
        User user = new User();
        user.setFullName(registerRequest.fullName());
        user.setEmail(registerRequest.email());

        user.setPassword(passwordEncoder.encode(registerRequest.password()));

        user.setRole(Role.USER);

        // db save
        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole());
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // email kontrol
        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException(
                        "INVALID_CREDENTIALS",
                        "E-posta veya şifre hatalı"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException(
                    "INVALID_CREDENTIALS",
                    "E-posta veya şifre hatalı");
        }

        String token = jwtService.generateToken(user);

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole());

        return new AuthResponse(
                token,
                "Bearer",
                86400L, // 1 day in seconds
                userResponse);

    }
}
