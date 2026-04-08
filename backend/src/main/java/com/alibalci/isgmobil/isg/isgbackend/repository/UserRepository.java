package com.alibalci.isgmobil.isg.isgbackend.repository;

import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); //optional kullanmamız sebebi bu emaiil olabilirde olamayabilirde diye belirtiyoruz
    boolean existsByEmail(String email);  // bu e posta var mı true ya da false ENTİTY ÇEKMEZ DAHA AVANTAJLI
    //userRepository.findByEmail(email).isPresent() buna göre
}
