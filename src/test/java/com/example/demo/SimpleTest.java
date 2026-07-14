package com.example.demo;

import com.example.demo.dto.request.RequestDTO;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.user.UserServiceImpl;

import org.junit.jupiter.api.Test;

// JUnitやMockitoを使わない、フレームワークなしのテスト例
// 実行方法: このファイルを開いてmainメソッド左の▷(Run)ボタンを押す
public class SimpleTest {

    @Test
    public void test() {
        UserRepository userRepository = new MockUserRepository();

        
    }
    


    static class MockUserRepository implements UserRepository {
        
        @Override
        public User findByEmail(String email) {
            return null;
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

}
