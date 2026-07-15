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
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationCreateRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.ObservationUpdateRequest;
import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.Observation;
import com.alibalci.isgmobil.isg.isgbackend.entity.ObservationStatus;
import com.alibalci.isgmobil.isg.isgbackend.entity.RiskCatalog;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
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

        companyRepository
                .findByIdAndUser(companyId, user)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        String photoUrl = photoStorageService.uploadPhoto(file);

        AiAnalysisResult aiResult = aiAnalysisService.analyzeImage(photoUrl);

        return new ObservationAnalyzeResponse(
                photoUrl,
                aiResult.getDescription(),
                aiResult.getRisks());
    }

    @Override
    public ObservationResponse confirmObservation(
            ObservationConfirmRequest request,
            User user) {

        Company company = companyRepository
                .findByIdAndUser(request.getCompanyId(), user)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        RiskCatalog risk = riskCatalogRepository
                .findById(request.getSelectedRiskCode())
                .orElseThrow(() -> new RuntimeException("Risk bulunamadı"));

        Integer riskScore = null;
        if (risk.getOlasilik() != null && risk.getSiddet() != null) {
            riskScore = risk.getOlasilik() * risk.getSiddet();
        }

        Integer residualRiskScore = null;
        if (risk.getOnlemSonrasiOlasilik() != null &&
                risk.getOnlemSonrasiSiddet() != null) {
            residualRiskScore = risk.getOnlemSonrasiOlasilik() * risk.getOnlemSonrasiSiddet();
        }

        Observation observation = Observation.builder()
                .photoUrl(request.getPhotoUrl())
                .description(request.getDescription())
                .aiDescription(request.getAiDescription())

                .aiRisk(risk.getTehlikeAdi())
                .aiSuggestions(formatSuggestions(risk.getOneriListesi()))

                .selectedRiskCode(risk.getTehlikeKodu())
                .selectedRiskName(risk.getTehlikeAdi())
                .possibleDamage(risk.getOlasiZarar())
                .suggestions(formatSuggestions(risk.getOneriListesi()))

                .probability(risk.getOlasilik())
                .severity(risk.getSiddet())
                .riskScore(riskScore)

                .postProbability(risk.getOnlemSonrasiOlasilik())
                .postSeverity(risk.getOnlemSonrasiSiddet())
                .residualRiskScore(residualRiskScore)

                .responsiblePerson(risk.getSorumluKisi())
                .dueDays(risk.getDuzeltmeSuresiGun())

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
    public ObservationResponse createObservation(
            ObservationCreateRequest request,
            MultipartFile file,
            User user) {

        Company company = companyRepository
                .findByIdAndUser(request.getCompanyId(), user)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        String photoUrl = photoStorageService.uploadPhoto(file);

        AiAnalysisResult aiResult = aiAnalysisService.analyzeImage(photoUrl);

        String aiSuggestions = null;
        String aiRisk = null;

        if (aiResult != null && aiResult.getRisks() != null && !aiResult.getRisks().isEmpty()) {
            aiSuggestions = aiResult.getRisks().stream()
                    .map(risk -> String.format(
                            "%s (%s) - score: %.4f",
                            risk.getName(),
                            risk.getCode(),
                            risk.getScore()))
                    .collect(Collectors.joining(" | "));

            aiRisk = aiResult.getRisks().get(0).getName();
        }

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
    @Transactional
    public ObservationResponse updateObservation(
            Long id,
            ObservationUpdateRequest request,
            User user) {

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
