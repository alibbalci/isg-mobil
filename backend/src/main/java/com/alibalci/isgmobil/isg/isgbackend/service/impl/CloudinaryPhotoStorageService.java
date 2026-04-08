package com.alibalci.isgmobil.isg.isgbackend.service.impl;


import com.alibalci.isgmobil.isg.isgbackend.service.PhotoStorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryPhotoStorageService implements PhotoStorageService {

    private final Cloudinary cloudinary;


    @Override
    public String uploadPhoto(MultipartFile photo) {
        try {

            Map uploadResult = cloudinary.uploader().upload(
                    photo.getBytes(),
                    ObjectUtils.emptyMap()
            );

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            throw new RuntimeException("Foto yüklenemedi"+e.getMessage());
        }
    }
}
