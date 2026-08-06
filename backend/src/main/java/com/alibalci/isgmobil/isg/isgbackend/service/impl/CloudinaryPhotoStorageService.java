package com.alibalci.isgmobil.isg.isgbackend.service.impl;


import com.alibalci.isgmobil.isg.isgbackend.exception.BadRequestException;
import com.alibalci.isgmobil.isg.isgbackend.exception.PhotoUploadException;
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
        if (photo == null || photo.isEmpty()) {
            throw new BadRequestException(
                    "PHOTO_REQUIRED",
                    "Yüklenecek fotoğraf boş olamaz");
        }

        try {

            Map uploadResult = cloudinary.uploader().upload(
                    photo.getBytes(),
                    ObjectUtils.emptyMap()
            );

            return uploadResult.get("secure_url").toString();

        } catch (Exception exception) {
            throw new PhotoUploadException(
                    "PHOTO_UPLOAD_FAILED",
                    "Fotoğraf yüklenemedi");
        }
    }
}
