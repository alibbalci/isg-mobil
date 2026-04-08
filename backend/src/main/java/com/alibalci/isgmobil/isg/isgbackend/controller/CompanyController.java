package com.alibalci.isgmobil.isg.isgbackend.controller;


import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyResponse;
import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import com.alibalci.isgmobil.isg.isgbackend.service.CompanyService;
import com.alibalci.isgmobil.isg.isgbackend.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                saved.getCreatedAt()
        );
    }

    @GetMapping
    public List<Company> getCompanies(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getCurrentUser(email);
        return companyService.getUserCompanies(user);
    }


    @DeleteMapping("/{id}")
    public void deleteCompany(@PathVariable Long id , Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getCurrentUser(email);
        companyService.deleteCompany(id, user);
    }

}
