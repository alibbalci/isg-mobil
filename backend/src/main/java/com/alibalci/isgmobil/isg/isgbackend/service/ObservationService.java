package com.alibalci.isgmobil.isg.isgbackend.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationAnalyzeResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationConfirmRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationUpdateRequest;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;

public interface ObservationService {

        ObservationAnalyzeResponse analyzeObservation(
                        Long companyId,
                        MultipartFile file,
                        User user);

        ObservationResponse confirmObservation(
                        Long observationId,
                        ObservationConfirmRequest request,
                        User user);

        ObservationResponse updateObservation(
                        Long id,
                        ObservationUpdateRequest request,
                        User user);

        Page<ObservationResponse> getUserObservations(User user, int page, int size);

        ObservationResponse getObservationById(Long id, User user);

        void deleteObservationById(Long id, User user);
}
