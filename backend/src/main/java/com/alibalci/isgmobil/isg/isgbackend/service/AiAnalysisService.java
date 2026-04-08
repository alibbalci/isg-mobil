package com.alibalci.isgmobil.isg.isgbackend.service;

import com.alibalci.isgmobil.isg.isgbackend.dto.AiAnalysisResult;

public interface AiAnalysisService {
    AiAnalysisResult analyzeImage(String photoUrl);
}


