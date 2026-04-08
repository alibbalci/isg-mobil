package com.alibalci.isgmobil.isg.isgbackend.dto;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AiAnalysisResult {
    private String description;
    private List<RiskItem> risks;
}