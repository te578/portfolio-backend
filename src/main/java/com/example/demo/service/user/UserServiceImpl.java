package com.example.demo.service.user;

import com.example.demo.dto.request.RequestDTO;
import com.example.demo.repository.UserRepository;
import com.example.demo.entity.User;
import com.example.demo.dto.response.ResponseDTO;
import com.example.demo.exception.UserNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.security.JwtUtil;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    //これはリポジトリを入れるための変数
    //今回はMyBatisを使うので、特にクラスを注入する必要はないこのような文法と覚える
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository) {

        //さっき作った変数に入れているが実態は何も入れていないmybatisが勝手に実装してくれるから
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = new JwtUtil();

    }

    // overrideして認証ロジックを実装
    @Override
    public String authenticate(RequestDTO requestDTO) {
        log.info("ログイン試行:");

        String email = requestDTO.getEmail();
        String password = requestDTO.getPassword();
        User userinfo = userRepository.findByEmail(email);

        if (userinfo == null) {
            log.warn("ユーザーが見つからない");
            throw new UserNotFoundException("ユーザーが見つかりません");
        }

        if (!passwordEncoder.matches(password, userinfo.getPassword())) {
            log.warn("パスワード不一致");
            throw new UserNotFoundException("パスワードが間違っています");
        }

        log.info("ログイン成功");
        String token = jwtUtil.generateToken(userinfo.getEmail());
        return token;
            

    }

    @Override
    public void register(RequestDTO requestDTO) {
        log.info("ユーザー登録試行:");

        User existUser = userRepository.findByEmail(requestDTO.getEmail());

        if (existUser != null) {
            log.warn("登録済みメールアドレス");
            throw new UserNotFoundException("すでにアカウントが登録されています");
        }

        String name = requestDTO.getName();
        String email = requestDTO.getEmail();
        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        userRepository.save(name, email, encodedPassword);
        log.info("ユーザー登録成功");


    }

}
