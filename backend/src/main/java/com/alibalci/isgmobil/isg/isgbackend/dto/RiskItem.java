package com.alibalci.isgmobil.isg.isgbackend.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RiskItem {

    private String code;

    private String name;

    private String damage;

    private List<String> suggestions;

    private double score;
}