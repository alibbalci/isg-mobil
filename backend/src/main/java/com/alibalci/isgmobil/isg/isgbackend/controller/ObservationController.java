package com.alibalci.isgmobil.isg.isgbackend.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationAnalyzeResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationConfirmRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationUpdateRequest;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import com.alibalci.isgmobil.isg.isgbackend.service.ObservationService;
import com.alibalci.isgmobil.isg.isgbackend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/observations")
@RequiredArgsConstructor
public class ObservationController {

    private final ObservationService observationService;
    private final UserService userService;

    @PostMapping(value = "/analyze", consumes = { "multipart/form-data" })
    public ObservationAnalyzeResponse analyzeObservation(
            @RequestParam("companyId") Long companyId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userService.getCurrentUser(email);

        return observationService.analyzeObservation(companyId, file, user);
    }

    @PostMapping("/confirm")
    public ObservationResponse confirmObservation(
            @RequestBody ObservationConfirmRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userService.getCurrentUser(email);

        return observationService.confirmObservation(request, user);
    }

    @GetMapping
    public Page<ObservationResponse> getUserObservations(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = authentication.getName();
        User user = userService.getCurrentUser(email);

        return observationService.getUserObservations(user, page, size);
    }

    @GetMapping("/{id}")
    public ObservationResponse getObservationById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userService.getCurrentUser(email);

        return observationService.getObservationById(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteObservation(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userService.getCurrentUser(email);

        observationService.deleteObservationById(id, user);
    }

    @PutMapping("/{id}")
    public ObservationResponse updateObservation(
            @PathVariable Long id,
            @RequestBody ObservationUpdateRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userService.getCurrentUser(email);

        return observationService.updateObservation(id, request, user);
    }
}