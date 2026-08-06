package com.alibalci.isgmobil.isg.isgbackend.exception;

public class ResourceNotFoundException
        extends BusinessException {

    public ResourceNotFoundException(
            String code,
            String message) {
        super(code, message);
    }
}