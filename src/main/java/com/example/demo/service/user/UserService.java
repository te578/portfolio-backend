package com.example.demo.service.user;

import com.example.demo.dto.request.RequestDTO;


public interface UserService {

    String authenticate(RequestDTO requestDTO);

    void register(RequestDTO requestDTO);

}