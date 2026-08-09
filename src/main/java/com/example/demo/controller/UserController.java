package com.example.demo.controller;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.request.RequestDTO;

import com.example.demo.service.user.UserService;
import com.example.demo.dto.response.ResponseDTO;
import com.example.demo.dto.TokenPair;

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

    //そのままDTOごと渡す
        TokenPair tokens = userService.authenticate(requestDTO);

        ResponseDTO res = new ResponseDTO();
        res.setAccessToken(tokens.getAccessToken());
        res.setRefreshToken(tokens.getRefreshToken());
        return ResponseEntity.ok(res);

    }

    //新規アカウント作成のエンドポイント
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RequestDTO requestDTO) {
     
        userService.register(requestDTO);
        return ResponseEntity.ok("ユーザー登録成功");
        
    }
    
    

}
