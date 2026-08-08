package com.alibalci.isgmobil.isg.isgbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record CompanyUpdateRequest(
        @NotBlank String name,
        String address,
        String hazardClass,
        String phone,
        String occupationalPhysician) {
}