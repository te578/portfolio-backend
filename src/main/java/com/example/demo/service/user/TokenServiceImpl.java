package com.example.demo.service.user;

import com.example.demo.dto.TokenPair;
import com.example.demo.entity.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.repository.TokenRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public TokenServiceImpl(JwtUtil jwtUtil, TokenRepository tokenRepository) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
    }

    public TokenPair getTokenPair(User userinfo) {
        // アクセストークン: JwtUtilで発行するJWT(短寿命、DBには保存しない)
        String accessToken = jwtUtil.generateToken(userinfo.getEmail());

        // リフレッシュトークン: ランダムな文字列(長寿命、DBに保存して後で照合する)
        String refreshToken = UUID.randomUUID().toString();
        
        // リフレッシュトークンをDBに保存する
        int userId = userinfo.getId();
        String tokenHash = this.hashToken(refreshToken);
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30); // 30日後

        tokenRepository.save(userId, tokenHash, expiresAt);

        TokenPair tokenPair = new TokenPair();
        tokenPair.setAccessToken(accessToken);
        tokenPair.setRefreshToken(refreshToken);
        return tokenPair;
    }


    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes());
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
