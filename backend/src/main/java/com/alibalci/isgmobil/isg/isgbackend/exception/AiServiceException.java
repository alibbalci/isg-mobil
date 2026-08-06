package com.alibalci.isgmobil.isg.isgbackend.exception;

public class AiServiceException
        extends BusinessException {

    public AiServiceException(
            String code,
            String message) {
        super(code, message);
    }
}