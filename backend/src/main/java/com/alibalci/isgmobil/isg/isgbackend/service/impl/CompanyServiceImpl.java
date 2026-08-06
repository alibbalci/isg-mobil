package com.alibalci.isgmobil.isg.isgbackend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import com.alibalci.isgmobil.isg.isgbackend.exception.ResourceNotFoundException;
import com.alibalci.isgmobil.isg.isgbackend.exception.UnauthorizedException;
import com.alibalci.isgmobil.isg.isgbackend.repository.CompanyRepository;
import com.alibalci.isgmobil.isg.isgbackend.service.CompanyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public Company createCompany(Company company, User user) {
        // şirketi login olan kullanıcıya bağla
        company.setUser(user);
        company.setCreatedAt(LocalDateTime.now());
        return companyRepository.save(company);
    }

    @Override
    public List<Company> getUserCompanies(User user) {
        return companyRepository.findByUser(user);
    }

    @Override
    public Company getCompanyById(Long id, User user) {
        return companyRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> companyNotFound(id));
    }

    @Override
    public void deleteCompany(Long id, User user) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> companyNotFound(id));

        // güvenlik kontrolü
        if (!company.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(
                    "COMPANY_ACCESS_DENIED",
                    "Bu şirketi silme yetkiniz yok");
        }
        companyRepository.delete(company);
    }

    @Override
    public Company updateCompany(Long id, Company updatedCompany, User user) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> companyNotFound(id));

        // güvenlik kontrolü
        if (!existingCompany.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(
                    "COMPANY_ACCESS_DENIED",
                    "Bu şirketi güncelleme yetkiniz yok");
        }

        existingCompany.setName(updatedCompany.getName());
        existingCompany.setAddress(updatedCompany.getAddress());
        existingCompany.setHazardClass(updatedCompany.getHazardClass());
        existingCompany.setPhone(updatedCompany.getPhone());
        existingCompany.setOccupationalPhysician(updatedCompany.getOccupationalPhysician());

        return companyRepository.save(existingCompany);
    }

    private ResourceNotFoundException companyNotFound(Long id) {
        return new ResourceNotFoundException(
                "COMPANY_NOT_FOUND",
                "Şirket bulunamadı: " + id);
    }
}
