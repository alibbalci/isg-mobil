package com.alibalci.isgmobil.isg.isgbackend.service;

import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationCreateRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationUpdateRequest;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ObservationService {
    ObservationResponse createObservation(
            ObservationCreateRequest request ,
            MultipartFile file,
            User user
    );

    ObservationResponse updateObservation(
            Long id,
            ObservationUpdateRequest request,
            User user
    );

    Page<ObservationResponse> getUserObservations(User user, int page, int size);
    ObservationResponse getObservationById(Long id , User user);
    void deleteObservationById(Long id , User user);

}
