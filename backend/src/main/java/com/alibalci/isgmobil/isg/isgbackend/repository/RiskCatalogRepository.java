package com.alibalci.isgmobil.isg.isgbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alibalci.isgmobil.isg.isgbackend.entity.RiskCatalog;

public interface RiskCatalogRepository extends JpaRepository<RiskCatalog, String> {
}