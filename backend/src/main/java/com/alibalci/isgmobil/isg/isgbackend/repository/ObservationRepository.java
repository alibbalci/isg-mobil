package com.alibalci.isgmobil.isg.isgbackend.repository;

import com.alibalci.isgmobil.isg.isgbackend.entity.Observation;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ObservationRepository extends JpaRepository<Observation, Long> {

    Page<Observation> findByUser(User user, Pageable pageable);

    Optional<Observation> findByIdAndUser(Long id, User user);
}