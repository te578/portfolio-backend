package com.example.demo.controller;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.request.RequestDTO;

import com.example.demo.service.user.UserService;
import com.example.demo.dto.response.ResponseDTO;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    // 初めのログインエンドポイント
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody RequestDTO requestDTO) {

        try {
        //そのままDTOごと渡す
            var val = userService.authenticate(requestDTO);

            ResponseDTO res = new ResponseDTO();
            res.setToken(val);
            return ResponseEntity.ok(res);

        } catch (RuntimeException e) {
            // エラーコードを返す(Auth_001 はユーザーが見つからないエラー)
            // ApiResponseクラスのdataをジェネリックにしているためVoidを指定しているobjectでもいいかなと考えている;
            return ResponseEntity.status(401).body(e.getMessage());

        } catch  (Exception e) {
            // その他のエラー
            return ResponseEntity.status(500).build();
        }

    }

    //新規アカウント作成のエンドポイント
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RequestDTO requestDTO) {
     
        try {
            userService.register(requestDTO);
            return ResponseEntity.ok("ユーザー登録成功");

        } catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    
    

}
