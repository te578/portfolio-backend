package com.example.demo.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Insert;



@Mapper
public interface TokenRepository {

    @Insert("INSERT INTO refresh_tokens (user_id, token_hash, expires_at) VALUES (#{userId}, #{tokenHash}, #{expiresAt})")
    int save(@Param("userId") int userId, @Param("tokenHash") String tokenHash, @Param("expiresAt") LocalDateTime expiresAt);

}
