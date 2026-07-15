package com.example.demo;

import com.example.demo.dto.request.RequestDTO;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.user.UserServiceImpl;


import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

// JUnitやMockitoを使わない、フレームワークなしのテスト例
// 実行方法: このファイルを開いてmainメソッド左の▷(Run)ボタンを押す
public class SimpleTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;


    @Test
    public void ユーザーがいない場合は例外が投げられる() {
        UserRepository userRepository = new MockUserRepository();
        UserServiceImpl userService = new UserServiceImpl(userRepository);

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setEmail("test@example.com");
        requestDTO.setPassword("password");
        assertThrows(RuntimeException.class, () -> {
            userService.authenticate(requestDTO);
        });
    }

    @Test
    public void  パスワードが不一致の場合は例外が投げられる() {
        UserRepository userRepository = new MockUserRepository();
        UserServiceImpl userService = new UserServiceImpl(userRepository);

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setEmail("test@example.com");
        requestDTO.setPassword("wrongpassword");
        assertThrows(RuntimeException.class, () -> {
            userService.authenticate(requestDTO);
        });
    }


    
}

class MockUserRepository implements UserRepository {
        
        @Override
        public User findByEmail(String email) {
            return null; // ユーザーが存在しない場合はnullを返す
        }

        @Override
        public int save(String name, String email, String password) {
            return 0;
        }

        @Override
        public Profile findProfileByEmail(String email) {
            return null;
        }
}



