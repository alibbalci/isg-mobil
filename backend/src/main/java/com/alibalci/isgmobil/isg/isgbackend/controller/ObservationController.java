package com.alibalci.isgmobil.isg.isgbackend.controller;


import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationCreateRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationUpdateRequest;
import com.alibalci.isgmobil.isg.isgbackend.entity.RiskLevel;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import com.alibalci.isgmobil.isg.isgbackend.service.ObservationService;
import com.alibalci.isgmobil.isg.isgbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/observations")
@RequiredArgsConstructor
public class ObservationController {

    private final ObservationService observationService;
    private final UserService userService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ObservationResponse createObservation(
            @RequestParam("description") String description,
            @RequestParam("riskLevel") RiskLevel riskLevel,
            @RequestParam("companyId") Long companyId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {

        String email = authentication.getName();
        User user = userService.getCurrentUser(email);

        ObservationCreateRequest request = new ObservationCreateRequest();
        request.setDescription(description);
        request.setRiskLevel(riskLevel);
        request.setCompanyId(companyId);

        return observationService.createObservation(request, file, user);
    }

    @GetMapping
    public Page<ObservationResponse> getUserObservations(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        String email = authentication.getName();
        User user = userService.getCurrentUser(email);

        return observationService.getUserObservations(user, page, size);
    }

    @GetMapping("/{id}")
    public ObservationResponse getObservationById(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String email = authentication.getName();
        User user = userService.getCurrentUser(email);

        return observationService.getObservationById(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteObservation(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String email = authentication.getName();
        User user = userService.getCurrentUser(email);

        observationService.deleteObservationById(id, user);
    }

    @PutMapping("/{id}")
    public ObservationResponse updateObservation(
            @PathVariable Long id ,
            @RequestBody ObservationUpdateRequest request ,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user = userService.getCurrentUser(email);
        return  observationService.updateObservation(id,request,user);
    }

}