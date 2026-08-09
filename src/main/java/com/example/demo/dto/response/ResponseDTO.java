package com.example.demo.dto.response;

import lombok.Data;

@Data
public class ResponseDTO {
    private String accessToken;
    private String refreshToken;
    private int    role;

}
