package com.example.demo;


import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.demo.service.user.UserServiceImpl;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.request.RequestDTO;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;


@ExtendWith(MockitoExtension.class)
 class UserServiceImplTest {

   @Mock
   UserRepository userRepository;

   @InjectMocks
    UserServiceImpl userService;

    @Test
    
    void ユーザーが存在しない場合は例外が投げられる() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setEmail("nonexistent@example.com");
        requestDTO.setPassword("password");

        assertThrows(RuntimeException.class, () -> {
            userService.authenticate(requestDTO);
        });
    }


    
}
