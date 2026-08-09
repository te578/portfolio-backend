package com.example.demo.service.user;

import com.example.demo.dto.request.RequestDTO;
import com.example.demo.dto.TokenPair;


public interface UserService {

    TokenPair authenticate(RequestDTO requestDTO);

    void register(RequestDTO requestDTO);

}