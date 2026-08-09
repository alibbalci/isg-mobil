package com.alibalci.isgmobil.isg.isgbackend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ObservationAnalyzeResponse {

    private Long observationId;

    private String photoUrl;

    private String status;

    private String aiDescription;

    private List<RiskItem> riskCandidates;
}
