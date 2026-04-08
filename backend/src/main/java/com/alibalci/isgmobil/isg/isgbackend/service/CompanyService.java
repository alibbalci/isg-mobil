package com.alibalci.isgmobil.isg.isgbackend.service;


import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CompanyService {

    // hangi firma hangi kullanıcıya ait bilmek icin icine User user ekle
    Company createCompany(Company company, User user);
    List<Company> getUserCompanies(User user);
    void deleteCompany(Long id, User user);
}
