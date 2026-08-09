package com.alibalci.isgmobil.isg.isgbackend.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibalci.isgmobil.isg.isgbackend.dto.AiAnalysisResult;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationAnalyzeResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationConfirmRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationUpdateRequest;
import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.Observation;
import com.alibalci.isgmobil.isg.isgbackend.entity.ObservationStatus;
import com.alibalci.isgmobil.isg.isgbackend.entity.RiskCatalog;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import com.alibalci.isgmobil.isg.isgbackend.exception.InvalidStatusTransitionException;
import com.alibalci.isgmobil.isg.isgbackend.exception.ResourceNotFoundException;
import com.alibalci.isgmobil.isg.isgbackend.repository.CompanyRepository;
import com.alibalci.isgmobil.isg.isgbackend.repository.ObservationRepository;
import com.alibalci.isgmobil.isg.isgbackend.repository.RiskCatalogRepository;
import com.alibalci.isgmobil.isg.isgbackend.service.AiAnalysisService;
import com.alibalci.isgmobil.isg.isgbackend.service.ObservationService;
import com.alibalci.isgmobil.isg.isgbackend.service.PhotoStorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ObservationServiceImpl implements ObservationService {

    private final ObservationRepository observationRepository;
    private final CompanyRepository companyRepository;
    private final RiskCatalogRepository riskCatalogRepository;
    private final PhotoStorageService photoStorageService;
    private final AiAnalysisService aiAnalysisService;

    @Override
    public ObservationAnalyzeResponse analyzeObservation(
            Long companyId,
            MultipartFile file,
            User user) {

        Company company = companyRepository
                .findByIdAndUser(companyId, user)
                .orElseThrow(() -> companyNotFound(companyId));

        Observation observation = Observation.builder()
                .status(ObservationStatus.PENDING_AI)
                .company(company)
                .user(user)
                .build();

        observation = observationRepository.save(observation);

        String photoUrl = photoStorageService.uploadPhoto(file);
        observation.setPhotoUrl(photoUrl);
        observation = observationRepository.save(observation);

        AiAnalysisResult aiResult = aiAnalysisService.analyzeImage(photoUrl);

        observation.setAiDescription(aiResult.getDescription());
        observation.setStatus(ObservationStatus.AI_ANALYZED);
        observation = observationRepository.save(observation);

        return new ObservationAnalyzeResponse(
                observation.getId(),
                photoUrl,
                observation.getStatus().name(),
                aiResult.getDescription(),
                aiResult.getRisks());
    }

    @Override
    public ObservationResponse confirmObservation(
            Long observationId,
            ObservationConfirmRequest request,
            User user) {

        Observation observation = observationRepository
                .findByIdAndUser(observationId, user)
                .orElseThrow(() -> observationNotFound(observationId));

        if (observation.getStatus() != ObservationStatus.AI_ANALYZED) {
            throw new InvalidStatusTransitionException(
                    "INVALID_STATUS_TRANSITION",
                    "Yalnızca AI_ANALYZED durumundaki gözlem onaylanabilir");
        }

        RiskCatalog risk = riskCatalogRepository
                .findById(request.getSelectedRiskCode())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "RISK_NOT_FOUND",
                        "Risk bulunamadı: " + request.getSelectedRiskCode()));

        Integer riskScore = null;
        if (risk.getOlasilik() != null && risk.getSiddet() != null) {
            riskScore = risk.getOlasilik() * risk.getSiddet();
        }

        Integer residualRiskScore = null;
        if (risk.getOnlemSonrasiOlasilik() != null &&
                risk.getOnlemSonrasiSiddet() != null) {
            residualRiskScore = risk.getOnlemSonrasiOlasilik() * risk.getOnlemSonrasiSiddet();
        }

        observation.setDescription(request.getDescription());
        observation.setAiRisk(risk.getTehlikeAdi());
        observation.setAiSuggestions(formatSuggestions(risk.getOneriListesi()));
        observation.setSelectedRiskCode(risk.getTehlikeKodu());
        observation.setSelectedRiskName(risk.getTehlikeAdi());
        observation.setPossibleDamage(risk.getOlasiZarar());
        observation.setSuggestions(formatSuggestions(risk.getOneriListesi()));
        observation.setProbability(risk.getOlasilik());
        observation.setSeverity(risk.getSiddet());
        observation.setRiskScore(riskScore);
        observation.setPostProbability(risk.getOnlemSonrasiOlasilik());
        observation.setPostSeverity(risk.getOnlemSonrasiSiddet());
        observation.setResidualRiskScore(residualRiskScore);
        observation.setResponsiblePerson(risk.getSorumluKisi());
        observation.setDueDays(risk.getDuzeltmeSuresiGun());
        observation.setStatus(ObservationStatus.CONFIRMED);
        observation.setConfirmedAt(LocalDateTime.now());

        Observation saved = observationRepository.save(observation);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ObservationResponse updateObservation(
            Long id,
            ObservationUpdateRequest request,
            User user) {

        Observation observation = observationRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> observationNotFound(id));

        if (request.getDescription() != null) {
            observation.setDescription(request.getDescription());
        }

        if (request.getRiskLevel() != null) {
            observation.setRiskLevel(request.getRiskLevel());
        }

        if (request.getStatus() != null) {
            ObservationStatus current = observation.getStatus();
            ObservationStatus incoming = request.getStatus();

            if (current == ObservationStatus.CONFIRMED &&
                    incoming == ObservationStatus.REVIEWED) {

                observation.setStatus(incoming);
                observation.setReviewedBy(user);
                observation.setReviewedAt(LocalDateTime.now());

            } else if (current == ObservationStatus.REVIEWED &&
                    (incoming == ObservationStatus.APPROVED ||
                            incoming == ObservationStatus.REJECTED)) {

                observation.setStatus(incoming);

            } else {
                throw new InvalidStatusTransitionException(
                        "INVALID_STATUS_TRANSITION",
                        "Geçersiz durum geçişi: " + current + " -> " + incoming);
            }
        }

        observation.setUpdatedAt(LocalDateTime.now());

        Observation saved = observationRepository.save(observation);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ObservationResponse> getUserObservations(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Observation> observations = observationRepository.findByUser(user, pageable);
        return observations.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ObservationResponse getObservationById(Long id, User user) {
        Observation observation = observationRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> observationNotFound(id));

        return mapToResponse(observation);
    }

    @Override
    public void deleteObservationById(Long id, User user) {
        Observation observation = observationRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> observationNotFound(id));

        observationRepository.delete(observation);
    }

    private ObservationResponse mapToResponse(Observation observation) {

        Long reviewedById = null;

        if (observation.getReviewedBy() != null) {
            reviewedById = observation.getReviewedBy().getId();
        }

        return ObservationResponse.builder()
                .id(observation.getId())

                // Foto & açıklama
                .photoUrl(observation.getPhotoUrl())
                .description(observation.getDescription())
                .aiDescription(observation.getAiDescription())

                // Eski alanlar
                .riskLevel(observation.getRiskLevel())
                .aiRisk(observation.getAiRisk())
                .aiSuggestion(formatSuggestions(observation.getAiSuggestions()))

                // Final risk alanları
                .selectedRiskCode(observation.getSelectedRiskCode())
                .selectedRiskName(observation.getSelectedRiskName())
                .possibleDamage(observation.getPossibleDamage())
                .suggestions(formatSuggestions(observation.getSuggestions()))

                .probability(observation.getProbability())
                .severity(observation.getSeverity())
                .riskScore(observation.getRiskScore())

                .postProbability(observation.getPostProbability())
                .postSeverity(observation.getPostSeverity())
                .residualRiskScore(observation.getResidualRiskScore())

                .responsiblePerson(observation.getResponsiblePerson())
                .dueDays(observation.getDueDays())

                // Status
                .status(observation.getStatus().name())

                // Tarihler
                .createdAt(observation.getCreatedAt())
                .confirmedAt(observation.getConfirmedAt())
                .reviewedAt(observation.getReviewedAt())

                // Company
                .companyId(observation.getCompany().getId())
                .companyName(observation.getCompany().getName())

                // User
                .userId(observation.getUser().getId())
                .userFullName(observation.getUser().getFullName())

                // Reviewer
                .reviewedBy(reviewedById)

                .build();
    }

    private ResourceNotFoundException companyNotFound(Long id) {
        return new ResourceNotFoundException(
                "COMPANY_NOT_FOUND",
                "Şirket bulunamadı: " + id);
    }

    private ResourceNotFoundException observationNotFound(Long id) {
        return new ResourceNotFoundException(
                "OBSERVATION_NOT_FOUND",
                "Gözlem bulunamadı: " + id);
    }

    private String formatSuggestions(String rawSuggestions) {
        if (rawSuggestions == null || rawSuggestions.isBlank()) {
            return rawSuggestions;
        }

        String cleaned = rawSuggestions.trim().replace("\\\"", "\"");
        boolean arrayLiteral = cleaned.startsWith("{") && cleaned.endsWith("}");

        if (arrayLiteral) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        String delimiter = arrayLiteral ? "\"\\s*,\\s*\"" : "\\s*\\|\\s*";
        if (!arrayLiteral && !cleaned.contains("|")) {
            return cleaned.replace("\"", "").replace("[", "").replace("]", "").trim();
        }

        return Arrays.stream(cleaned.split(delimiter))
                .map(suggestion -> suggestion.replace("\"", "").replace("[", "").replace("]", "").trim())
                .filter(suggestion -> !suggestion.isBlank())
                .collect(Collectors.joining("\n"));
    }
}
