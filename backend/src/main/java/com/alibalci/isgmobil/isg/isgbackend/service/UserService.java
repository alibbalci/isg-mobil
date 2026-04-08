package com.alibalci.isgmobil.isg.isgbackend.service;

import com.alibalci.isgmobil.isg.isgbackend.dto.UserResponse;
import com.alibalci.isgmobil.isg.isgbackend.entity.User;

public interface UserService {

    User getCurrentUser(String email);

}