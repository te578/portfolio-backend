package com.example.demo.dto.request;

import lombok.Data;

@Data
public class RequestDTO {
    //privateはアクセス修飾子で、外部から直接アクセスできないようにするためのもの
    private String email;
    private String password;
    private String name;
    
}
