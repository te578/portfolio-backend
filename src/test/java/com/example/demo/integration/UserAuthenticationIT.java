package com.example.demo.integration;

import com.example.demo.dto.request.RequestDTO;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.service.user.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Testcontainersで本物のPostgreSQLをDockerコンテナとして起動し、
// モックを使わずFlywayのマイグレーションからDBまで通しで確認する結合テスト
@SpringBootTest
@Testcontainers
class UserAuthenticationIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    UserService userService;

    @Test
    void 存在しないユーザーでログインすると例外が投げられる() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setEmail("nonexistent@example.com");
        requestDTO.setPassword("password");

        assertThrows(UserNotFoundException.class, () -> {
            userService.authenticate(requestDTO);
        });
    }

    @Test
    void 登録したユーザーで正しい認証情報を使うとトークンが返る() {
        RequestDTO registerDTO = new RequestDTO();
        registerDTO.setName("テストユーザー");
        registerDTO.setEmail("integration-test@example.com");
        registerDTO.setPassword("password123");

        userService.register(registerDTO);

        RequestDTO loginDTO = new RequestDTO();
        loginDTO.setEmail("integration-test@example.com");
        loginDTO.setPassword("password123");

        String token = userService.authenticate(loginDTO);

        assertNotNull(token);
    }
}
