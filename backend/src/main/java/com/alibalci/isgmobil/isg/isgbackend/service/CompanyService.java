package com.alibalci.isgmobil.isg.isgbackend.service;

import java.util.List;

import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyCreateRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyUpdateRequest;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;

public interface CompanyService {
    CompanyResponse createCompany(CompanyCreateRequest request, User user);

    List<CompanyResponse> getUserCompanies(User user);

    CompanyResponse getCompanyById(Long id, User user);

    void deleteCompany(Long id, User user);

    CompanyResponse updateCompany(Long id, CompanyUpdateRequest request, User user);

}
