package com.alibalci.isgmobil.isg.isgbackend.dto;


import com.alibalci.isgmobil.isg.isgbackend.entity.Role;

public record UserResponse(

        Long id,
        String fullName,
        String email,
        Role role

) {}