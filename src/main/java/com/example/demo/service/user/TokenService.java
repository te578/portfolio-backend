package com.example.demo.service.user;

import com.example.demo.dto.TokenPair;
import com.example.demo.entity.User;

public interface TokenService {

    TokenPair getTokenPair(User userinfo);
    
}
