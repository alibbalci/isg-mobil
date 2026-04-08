package com.alibalci.isgmobil.isg.isgbackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoStorageService {
    String uploadPhoto(MultipartFile photo);
}
