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
    void 想定外のエラーの場合は500と共通のRFC9457形式の共通エラーが返る() throws Exception {
        when(userService.authenticate(any(RequestDTO.class)))
            .thenThrow(new RuntimeException());

           String requestBody = "{\"email\":\"user@example.com\",\"password\":\"password\"}";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.detail").value("an unexpected error occurred"))
            .andExpect(jsonPath("$.errorCode").value("INTERNAL_ERROR"));
    }
}
