package com.alibalci.isgmobil.isg.isgbackend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "observations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Observation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String photoUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String aiDescription;

    // Eski alanlar: şimdilik silmiyoruz
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    private String aiRisk;

    @Column(columnDefinition = "TEXT")
    private String aiSuggestions;

    // Final risk alanları
    private String selectedRiskCode;

    private String selectedRiskName;

    @Column(columnDefinition = "TEXT")
    private String possibleDamage;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    private Integer probability;

    private Integer severity;

    private Integer riskScore;

    private Integer postProbability;

    private Integer postSeverity;

    private Integer residualRiskScore;

    private String responsiblePerson;

    private Integer dueDays;

    @Enumerated(EnumType.STRING)
    private ObservationStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime confirmedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();

        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
