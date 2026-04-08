package com.alibalci.isgmobil.isg.isgbackend.service.impl;

import com.alibalci.isgmobil.isg.isgbackend.dto.AiAnalysisResult;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationCreateRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationUpdateRequest;
import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.Observation;
import com.alibalci.isgmobil.isg.isgbackend.entity.ObservationStatus;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import com.alibalci.isgmobil.isg.isgbackend.repository.CompanyRepository;
import com.alibalci.isgmobil.isg.isgbackend.repository.ObservationRepository;
import com.alibalci.isgmobil.isg.isgbackend.service.AiAnalysisService;
import com.alibalci.isgmobil.isg.isgbackend.service.ObservationService;
import com.alibalci.isgmobil.isg.isgbackend.service.PhotoStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObservationServiceImpl implements ObservationService {

    private final ObservationRepository observationRepository;
    private final CompanyRepository companyRepository;
    private final PhotoStorageService photoStorageService;
    private final AiAnalysisService aiAnalysisService;

    @Override
    public ObservationResponse createObservation(
            ObservationCreateRequest request,
            MultipartFile file,
            User user
    ) {
        Company company = companyRepository
                .findByIdAndUser(request.getCompanyId(), user)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // 1) Fotoğrafı yükle
        String photoUrl = photoStorageService.uploadPhoto(file);

        // 2) Python AI servisini çağır
        AiAnalysisResult aiResult = aiAnalysisService.analyzeImage(photoUrl);
        System.out.println("DESC: " + aiResult.getDescription());
        System.out.println("RISKS: " + aiResult.getRisks());

        // 3) Risk listesini text'e çevir
        String aiSuggestions = null;
        String aiRisk = null;

        if (aiResult != null && aiResult.getRisks() != null && !aiResult.getRisks().isEmpty()) {
            aiSuggestions = aiResult.getRisks().stream()
                    .map(risk -> String.format(
                            "%s (%s) - score: %.4f",
                            risk.getName(),
                            risk.getCode(),
                            risk.getScore()
                    ))
                    .collect(Collectors.joining(" | "));

            // En yakın ilk risk'i ana AI risk olarak kaydet
            aiRisk = aiResult.getRisks().get(0).getName();
        }

        // 4) Description artık request'ten değil AI'dan gelsin
        String finalDescription = request.getDescription();
        if (aiResult != null && aiResult.getDescription() != null && !aiResult.getDescription().isBlank()) {
            finalDescription = aiResult.getDescription();
        }

        Observation observation = Observation.builder()
                .photoUrl(photoUrl)
                .description(finalDescription)
                .riskLevel(request.getRiskLevel())
                .aiRisk(aiRisk)
                .aiSuggestions(aiSuggestions)
                .status(ObservationStatus.AI_ANALYZED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .company(company)
                .user(user)
                .build();

        Observation saved = observationRepository.save(observation);
        return mapToResponse(saved);
    }

    @Override
    public ObservationResponse updateObservation(
            Long id,
            ObservationUpdateRequest request,
            User user
    ) {
        Observation observation = observationRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Observation not found"));

        if (request.getDescription() != null) {
            observation.setDescription(request.getDescription());
        }

        if (request.getRiskLevel() != null) {
            observation.setRiskLevel(request.getRiskLevel());
        }

        if (request.getStatus() != null) {
            ObservationStatus current = observation.getStatus();
            ObservationStatus incoming = request.getStatus();

            if (current == ObservationStatus.AI_ANALYZED &&
                    incoming == ObservationStatus.REVIEWED) {

                observation.setStatus(incoming);
                observation.setReviewedBy(user);
                observation.setReviewedAt(LocalDateTime.now());

            } else if (current == ObservationStatus.REVIEWED &&
                    (incoming == ObservationStatus.APPROVED ||
                            incoming == ObservationStatus.REJECTED)) {

                observation.setStatus(incoming);

            } else {
                throw new RuntimeException("Geçersiz status geçişi");
            }
        }

        observation.setUpdatedAt(LocalDateTime.now());

        Observation saved = observationRepository.save(observation);
        return mapToResponse(saved);
    }

    @Override
    public Page<ObservationResponse> getUserObservations(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Observation> observations = observationRepository.findByUser(user, pageable);
        return observations.map(this::mapToResponse);
    }

    @Override
    public ObservationResponse getObservationById(Long id, User user) {
        Observation observation = observationRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Observation bulunamadı"));

        return mapToResponse(observation);
    }

    @Override
    public void deleteObservationById(Long id, User user) {
        Observation observation = observationRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Observation bulunamadı"));

        observationRepository.delete(observation);
    }

    private ObservationResponse mapToResponse(Observation observation) {
        return ObservationResponse.builder()
                .id(observation.getId())
                .photoUrl(observation.getPhotoUrl())
                .description(observation.getDescription())
                .riskLevel(observation.getRiskLevel())
                .aiRisk(observation.getAiRisk())
                .aiSuggestion(observation.getAiSuggestions())
                .status(observation.getStatus().name())
                .createdAt(observation.getCreatedAt())
                .companyId(observation.getCompany().getId())
                .companyName(observation.getCompany().getName())
                .userId(observation.getUser().getId())
                .userFullName(observation.getUser().getFullName())
                .build();
    }
}