package com.alibalci.isgmobil.isg.isgbackend.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @Email(message = "Email format is invalid")
        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank
        String password

) {}
