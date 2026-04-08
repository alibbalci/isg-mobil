package com.alibalci.isgmobil.isg.isgbackend.service.impl;

import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import com.alibalci.isgmobil.isg.isgbackend.repository.CompanyRepository;
import com.alibalci.isgmobil.isg.isgbackend.repository.UserRepository;
import com.alibalci.isgmobil.isg.isgbackend.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    public void deleteCompany(Long id, User user) {
        Company company = companyRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Company noy found "));

        // güvenlik kontrolü
        if (!company.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot delete this company");
        }
        companyRepository.delete(company);
    }
}
