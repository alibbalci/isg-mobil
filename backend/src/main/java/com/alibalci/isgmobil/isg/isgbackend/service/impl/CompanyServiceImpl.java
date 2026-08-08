package com.alibalci.isgmobil.isg.isgbackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyCreateRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyUpdateRequest;
import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import com.alibalci.isgmobil.isg.isgbackend.exception.ResourceNotFoundException;
import com.alibalci.isgmobil.isg.isgbackend.mapper.CompanyMapper;
import com.alibalci.isgmobil.isg.isgbackend.repository.CompanyRepository;
import com.alibalci.isgmobil.isg.isgbackend.service.CompanyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    @Transactional
    public CompanyResponse createCompany(CompanyCreateRequest request, User user) {
        Company company = companyMapper.toEntity(request, user);
        return companyMapper.toResponse(companyRepository.save(company));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponse> getUserCompanies(User user) {
        return companyRepository.findByUser(user)
                .stream()
                .map(companyMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long id, User user) {
        return companyMapper.toResponse(findOwnedCompany(id, user));
    }

    @Override
    @Transactional
    public void deleteCompany(Long id, User user) {
        companyRepository.delete(findOwnedCompany(id, user));
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Long id, CompanyUpdateRequest request, User user) {
        Company company = findOwnedCompany(id, user);
        companyMapper.updateEntity(request, company);
        return companyMapper.toResponse(companyRepository.saveAndFlush(company));
    }

    private Company findOwnedCompany(Long id, User user) {
        return companyRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "COMPANY_NOT_FOUND",
                        "Şirket bulunamadı: " + id));
    }
}
