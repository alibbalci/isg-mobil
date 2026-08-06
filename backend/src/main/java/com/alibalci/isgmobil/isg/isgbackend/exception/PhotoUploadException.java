package com.alibalci.isgmobil.isg.isgbackend.exception;

public class PhotoUploadException
        extends BusinessException {

    public PhotoUploadException(
            String code,
            String message) {
        super(code, message);
    }
}