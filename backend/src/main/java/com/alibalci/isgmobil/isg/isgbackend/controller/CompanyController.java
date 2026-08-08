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

import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyCreateRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyUpdateRequest;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import com.alibalci.isgmobil.isg.isgbackend.service.CompanyService;
import com.alibalci.isgmobil.isg.isgbackend.service.UserService;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor

public class CompanyController {

    private final CompanyService companyService;
    private final UserService userService;

    @PostMapping
    public CompanyResponse createCompany(@Valid @RequestBody CompanyCreateRequest request,
            Authentication authentication) {
        return companyService.createCompany(request, getCurrentUser(authentication));
    }

    @GetMapping
    public List<CompanyResponse> getCompanies(Authentication authentication) {
        return companyService.getUserCompanies(getCurrentUser(authentication));
    }

    @GetMapping("/{id}")
    public CompanyResponse getCompanyById(@PathVariable Long id,
            Authentication authentication) {
        return companyService.getCompanyById(id, getCurrentUser(authentication));
    }

    @DeleteMapping("/{id}")
    public void deleteCompany(@PathVariable Long id, Authentication authentication) {
        companyService.deleteCompany(id, getCurrentUser(authentication));
    }

    @PutMapping("/{id}")
    public CompanyResponse updateCompany(@PathVariable Long id,
            @Valid @RequestBody CompanyUpdateRequest request,
            Authentication authentication) {
        return companyService.updateCompany(id, request, getCurrentUser(authentication));
    }

    private User getCurrentUser(Authentication authentication) {
        return userService.getCurrentUser(authentication.getName());
    }
}
