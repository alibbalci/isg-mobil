package com.alibalci.isgmobil.isg.isgbackend.repository;

import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByUser(User user);
    Optional<Company> findByIdAndUser(Long id, User user);
}


