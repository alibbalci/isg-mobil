package com.alibalci.isgmobil.isg.isgbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "risk_catalog")
public class RiskCatalog {

    @Id
    @Column(name = "tehlike_kodu")
    private String tehlikeKodu;

    @Column(name = "tehlike_adi")
    private String tehlikeAdi;

    @Column(name = "kategori")
    private String kategori;

    @Column(name = "olasi_zarar", columnDefinition = "TEXT")
    private String olasiZarar;

    @Column(name = "oneri_listesi", columnDefinition = "TEXT")
    private String oneriListesi;

    @Column(name = "olasilik")
    private Integer olasilik;

    @Column(name = "siddet")
    private Integer siddet;

    @Column(name = "sorumlu_kisi")
    private String sorumluKisi;

    @Column(name = "duzeltme_suresi_gun")
    private Integer duzeltmeSuresiGun;

    @Column(name = "onlem_sonrasi_olasilik")
    private Integer onlemSonrasiOlasilik;

    @Column(name = "onlem_sonrasi_siddet")
    private Integer onlemSonrasiSiddet;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;
}
