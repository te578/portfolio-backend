package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.dto.request.RequestDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.user.UserServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void ユーザーが存在しない場合は例外が投げられる() {
        Mockito.when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setEmail("nonexistent@example.com");
        requestDTO.setPassword("password");

        assertThrows(RuntimeException.class, () -> {
            userService.authenticate(requestDTO);
        });
    }

    @Test
    void 正しい認証情報の場合はトークンが返る() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(new BCryptPasswordEncoder().encode("password"));

        Mockito.when(userRepository.findByEmail("user@example.com")).thenReturn(user);

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setEmail("user@example.com");
        requestDTO.setPassword("password");

        String token = userService.authenticate(requestDTO);

        assertNotNull(token);
    }
}
