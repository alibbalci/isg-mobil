package com.alibalci.isgmobil.isg.isgbackend.dto;

import com.alibalci.isgmobil.isg.isgbackend.entity.RiskLevel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ObservationResponse {
    private Long id ;
    private String photoUrl;
    private String description;
    private RiskLevel riskLevel;
    private String aiRisk;
    private String aiSuggestion;
    private String status;
    private LocalDateTime createdAt;
    private Long companyId;
    private String companyName;
    private Long userId;
    private String userFullName;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
}
