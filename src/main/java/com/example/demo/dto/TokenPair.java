package com.example.demo.dto;

import lombok.Data;

@Data
public class TokenPair {
    private String accessToken;
    private String refreshToken;

}