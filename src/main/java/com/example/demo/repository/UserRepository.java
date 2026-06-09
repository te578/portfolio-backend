package com.example.demo.repository;


import com.example.demo.entity.User;
import com.example.demo.entity.Profile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface UserRepository {

    // パラメータが1つのときは@Paramなしでも#{email}に自動でマッピングされる
    // DBのカラム名はpassだがJava側のフィールド名はpasswordなのでAS passwordでマッピング
    @Select("SELECT id, email, pass AS password FROM users WHERE email = #{email}")
    User findByEmail(String email);

    // パラメータが複数のときは@Paramがないとどれが#{name}等か判断できないので必須
    // DBのカラム名はpassなので注意
    @Insert("INSERT INTO users (name, email, pass) VALUES (#{name}, #{email}, #{password})")
    int save(@Param("name") String name, @Param("email") String email, @Param("password") String password);
    

    @Select("SELECT id, name, email FROM users WHERE email = #{email}")
    Profile findProfileByEmail(String email);
}
