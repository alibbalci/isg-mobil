package com.alibalci.isgmobil.isg.isgbackend.dto;

import java.time.LocalDateTime;

import com.alibalci.isgmobil.isg.isgbackend.entity.RiskLevel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ObservationResponse {

    private Long id;
    private String photoUrl;
    private String description;
    private String aiDescription;

    // Eski alanlar
    private RiskLevel riskLevel;
    private String aiRisk;
    private String aiSuggestion;

    // Final risk alanları
    private String selectedRiskCode;
    private String selectedRiskName;
    private String possibleDamage;
    private String suggestions;

    private Integer probability;
    private Integer severity;
    private Integer riskScore;

    private Integer postProbability;
    private Integer postSeverity;
    private Integer residualRiskScore;

    private String responsiblePerson;
    private Integer dueDays;

    private String status;
    private LocalDateTime createdAt;

    private Long companyId;
    private String companyName;

    private Long userId;
    private String userFullName;

    private Long reviewedBy;
    private LocalDateTime reviewedAt;
}