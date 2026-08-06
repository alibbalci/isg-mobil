package com.alibalci.isgmobil.isg.isgbackend.exception;

public class UnauthorizedException
        extends BusinessException {

    public UnauthorizedException(
            String code,
            String message) {
        super(code, message);
    }
}