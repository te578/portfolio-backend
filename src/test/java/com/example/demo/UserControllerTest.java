package com.example.demo;

import com.example.demo.controller.UserController;
import com.example.demo.dto.request.RequestDTO;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.service.user.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Test
    void ログイン失敗時のレスポンス全体を確認する() throws Exception {
        when(userService.authenticate(any(RequestDTO.class)))
            .thenThrow(new UserNotFoundException("ユーザーが見つかりません"));

        String requestBody = "{\"email\":\"nonexistent@example.com\",\"password\":\"password\"}";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andDo(print());  // ← ステータス・ヘッダー・body全部をコンソールに出力する
    }

    @Test
    void ログイン成功時は200とトークンが返る() throws Exception {
        when(userService.authenticate(any(RequestDTO.class))).thenReturn("dummy-token");

        String requestBody = "{\"email\":\"user@example.com\",\"password\":\"password\"}";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("dummy-token"));
    }
}
