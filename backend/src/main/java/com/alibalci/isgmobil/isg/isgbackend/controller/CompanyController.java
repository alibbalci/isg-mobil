package com.alibalci.isgmobil.isg.isgbackend.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyResponse;
import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import com.alibalci.isgmobil.isg.isgbackend.service.CompanyService;
import com.alibalci.isgmobil.isg.isgbackend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor

public class CompanyController {

    private final CompanyService companyService;
    private final UserService userService;

    @PostMapping
    public CompanyResponse createCompany(@RequestBody Company company,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userService.getCurrentUser(email);

        Company saved = companyService.createCompany(company, user);

        return new CompanyResponse(
                saved.getId(),
                saved.getName(),
                saved.getAddress(),
                saved.getHazardClass(),
                saved.getPhone(),
                saved.getOccupationalPhysician(),
                saved.getCreatedAt());
    }

    @GetMapping
    public List<Company> getCompanies(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getCurrentUser(email);
        return companyService.getUserCompanies(user);
    }

    @GetMapping("/{id}")
    public CompanyResponse getCompanyById(@PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getCurrentUser(email);
        Company company = companyService.getCompanyById(id, user);

        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getAddress(),
                company.getHazardClass(),
                company.getPhone(),
                company.getOccupationalPhysician(),
                company.getCreatedAt());
    }

    @DeleteMapping("/{id}")
    public void deleteCompany(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getCurrentUser(email);
        companyService.deleteCompany(id, user);
    }

    @PutMapping("/{id}")
    public CompanyResponse updateCompany(@PathVariable Long id,
            @RequestBody Company company,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getCurrentUser(email);
        Company updated = companyService.updateCompany(id, company, user);

        return new CompanyResponse(
                updated.getId(),
                updated.getName(),
                updated.getAddress(),
                updated.getHazardClass(),
                updated.getPhone(),
                updated.getOccupationalPhysician(),
                updated.getCreatedAt());
    }

}
