package com.alibalci.isgmobil.isg.isgbackend.dto;

import com.alibalci.isgmobil.isg.isgbackend.entity.ObservationStatus;
import com.alibalci.isgmobil.isg.isgbackend.entity.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ObservationUpdateRequest {
    private String description;
    private RiskLevel riskLevel;
    private ObservationStatus status;

}
