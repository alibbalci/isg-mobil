package com.alibalci.isgmobil.isg.isgbackend.dto;

import java.time.LocalDateTime;

public record CompanyResponse(
        Long id,
        String name,
        String address,
        String hazardClass,
        String phone,
        String occupationalPhysician,
        LocalDateTime createdAt
) {}