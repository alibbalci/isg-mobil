package com.alibalci.isgmobil.isg.isgbackend.service.impl;

import com.alibalci.isgmobil.isg.isgbackend.dto.AiAnalysisResult;
import com.alibalci.isgmobil.isg.isgbackend.dto.AiRequest;
import com.alibalci.isgmobil.isg.isgbackend.service.AiAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {

    @Override
    public AiAnalysisResult analyzeImage(String imageUrl) {

        String url = "http://127.0.0.1:8001/analyze";

        RestTemplate restTemplate = new RestTemplate();

        AiRequest request = new AiRequest(imageUrl);

        ResponseEntity<AiAnalysisResult> response =
                restTemplate.postForEntity(url, request, AiAnalysisResult.class);

        return response.getBody();
    }

}
