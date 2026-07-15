package com.alibalci.isgmobil.isg.isgbackend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;

@Service
public interface CompanyService {

    // hangi firma hangi kullanıcıya ait bilmek icin icine User user ekle
    Company createCompany(Company company, User user);

    List<Company> getUserCompanies(User user);

    Company getCompanyById(Long id, User user);

    void deleteCompany(Long id, User user);

    Company updateCompany(Long id, Company updatedCompany, User user);

}
