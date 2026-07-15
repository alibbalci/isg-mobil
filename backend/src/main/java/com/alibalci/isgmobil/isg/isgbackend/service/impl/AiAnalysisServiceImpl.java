package com.alibalci.isgmobil.isg.isgbackend.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibalci.isgmobil.isg.isgbackend.dto.AiAnalysisResult;
import com.alibalci.isgmobil.isg.isgbackend.dto.AiRequest;
import com.alibalci.isgmobil.isg.isgbackend.service.AiAnalysisService;

@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    @Override
    public AiAnalysisResult analyzeImage(String imageUrl) {

        String url = aiServiceUrl + "/analyze";

        AiRequest request = new AiRequest(imageUrl);

        ResponseEntity<AiAnalysisResult> response = restTemplate.postForEntity(url, request, AiAnalysisResult.class);

        return response.getBody();
    }
}