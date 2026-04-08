package com.alibalci.isgmobil.isg.isgbackend.dto;


import com.alibalci.isgmobil.isg.isgbackend.entity.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Client hepsini göndermemeli cünkü bazılarını backend üretir .
@Data
public class ObservationCreateRequest {

    @NotBlank(message = "Description boş olamaz")
    private String description;

    @NotNull(message = "Risk level zorunludur")
    private RiskLevel riskLevel;

    @NotNull(message = "CompanyId zorunludur")
    private Long companyId;
}
