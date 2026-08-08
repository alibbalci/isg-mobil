package com.alibalci.isgmobil.isg.isgbackend.mapper;

import org.springframework.stereotype.Component;

import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyCreateRequest;
import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyUpdateRequest;
import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;

@Component
public class CompanyMapper {

    public Company toEntity(CompanyCreateRequest request, User user) {
        Company company = new Company();
        company.setName(request.name());
        company.setAddress(request.address());
        company.setHazardClass(request.hazardClass());
        company.setPhone(request.phone());
        company.setOccupationalPhysician(request.occupationalPhysician());
        company.setUser(user);
        return company;
    }

    public void updateEntity(CompanyUpdateRequest request, Company company) {
        company.setName(request.name());
        company.setAddress(request.address());
        company.setHazardClass(request.hazardClass());
        company.setPhone(request.phone());
        company.setOccupationalPhysician(request.occupationalPhysician());
    }

    public CompanyResponse toResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getAddress(),
                company.getHazardClass(),
                company.getPhone(),
                company.getOccupationalPhysician(),
                company.getCreatedAt(),
                company.getUpdatedAt());
    }
}
