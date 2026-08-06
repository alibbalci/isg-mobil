package com.alibalci.isgmobil.isg.isgbackend.exception;

public class InvalidStatusTransitionException
        extends BusinessException {

    public InvalidStatusTransitionException(
            String code,
            String message) {
        super(code, message);
    }
}