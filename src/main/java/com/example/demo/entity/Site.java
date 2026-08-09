package com.example.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Site {
    private int siteId;
    private String companyCd;
    private String companyNm;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
