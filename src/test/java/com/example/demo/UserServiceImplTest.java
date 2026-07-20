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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import com.example.demo.exception.UserNotFoundException;

// JUnitやMockitoを使わない、フレームワークなしのテスト例
// 実行方法: このファイルを開いてmainメソッド左の▷(Run)ボタンを押す

@ExtendWith(MockitoExtension.class) 
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void ユーザーが存在しない場合のテスト() {
        // ユーザーが存在しない場合のテスト
        Mockito.when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);
        var requestDTO = new RequestDTO();
        requestDTO.setEmail("nonexistent@example.com");
        requestDTO.setPassword("password");

        assertThrows(UserNotFoundException.class, () -> {
            userService.authenticate(requestDTO);
        });

    }

    @Test
    public void パスワードが間違っている場合のテスト() {

    }

    @Test
    public void 正しい認証の場合のtokenが返されるテスト() {

    }

    // 他のテストケースもここに追加できます
    // 例: ユーザー登録のテスト、JWTトークンの生成テストなど
    

    

    
}




