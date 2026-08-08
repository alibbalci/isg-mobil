package com.alibalci.isgmobil.isg.isgbackend.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyResponse;
import com.alibalci.isgmobil.isg.isgbackend.dto.CompanyUpdateRequest;
import com.alibalci.isgmobil.isg.isgbackend.entity.Company;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import com.alibalci.isgmobil.isg.isgbackend.exception.ResourceNotFoundException;
import com.alibalci.isgmobil.isg.isgbackend.mapper.CompanyMapper;
import com.alibalci.isgmobil.isg.isgbackend.repository.CompanyRepository;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    private CompanyServiceImpl companyService;
    private User owner;

    @BeforeEach
    void setUp() {
        companyService = new CompanyServiceImpl(companyRepository, new CompanyMapper());
        owner = new User();
        owner.setId(10L);
    }

    @Test
    void getCompanyByIdReturnsOnlyOwnedCompany() {
        Company company = company(1L, "Mevcut Şirket");
        when(companyRepository.findByIdAndUser(1L, owner)).thenReturn(Optional.of(company));

        CompanyResponse response = companyService.getCompanyById(1L, owner);

        assertEquals(1L, response.id());
        assertEquals("Mevcut Şirket", response.name());
        verify(companyRepository, never()).findById(1L);
    }

    @Test
    void getCompanyByIdHidesCompaniesNotOwnedByUser() {
        when(companyRepository.findByIdAndUser(99L, owner)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> companyService.getCompanyById(99L, owner));

        verify(companyRepository, never()).findById(99L);
    }

    @Test
    void updateCompanyUpdatesAllowedFields() {
        Company company = company(1L, "Eski Şirket");
        CompanyUpdateRequest request = new CompanyUpdateRequest(
                "Yeni Şirket", "Yeni adres", "Tehlikeli", "555", "Dr. Test");
        when(companyRepository.findByIdAndUser(1L, owner)).thenReturn(Optional.of(company));
        when(companyRepository.saveAndFlush(company)).thenReturn(company);

        CompanyResponse response = companyService.updateCompany(1L, request, owner);

        assertEquals("Yeni Şirket", response.name());
        assertEquals("Yeni adres", response.address());
        assertEquals("Tehlikeli", response.hazardClass());
        assertEquals(owner, company.getUser());
        verify(companyRepository).saveAndFlush(company);
    }

    @Test
    void deleteCompanyUsesOwnershipQuery() {
        Company company = company(1L, "Silinecek Şirket");
        when(companyRepository.findByIdAndUser(1L, owner)).thenReturn(Optional.of(company));

        companyService.deleteCompany(1L, owner);

        verify(companyRepository).delete(company);
        verify(companyRepository, never()).findById(1L);
    }

    private Company company(Long id, String name) {
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        company.setUser(owner);
        company.setCreatedAt(LocalDateTime.now().minusDays(1));
        company.setUpdatedAt(LocalDateTime.now());
        return company;
    }
}
