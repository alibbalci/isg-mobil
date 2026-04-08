package com.alibalci.isgmobil.isg.isgbackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RiskItem {
    private String code;
    private String name;
    private double score;
}