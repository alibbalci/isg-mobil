package com.alibalci.isgmobil.isg.isgbackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ObservationConfirmRequest {

    private String description;

    private String selectedRiskCode;
}
