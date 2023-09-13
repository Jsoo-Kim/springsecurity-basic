package com.example.springsecurity.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {
    // 사용자 이름, 비밀 키, 토큰의 만료시간을 사용하여 JWT(JSON Web Token) 생성
    public static String createToken(String userName, String key, long expireTimeMs) {
        Claims claims = Jwts.claims(); // 맵처럼 사용. 사용자 이름을 저장할 곳
        claims.put("userName", userName); // JWT의 클레임에 사용자 이름 추가. 키-값 형태

        return Jwts.builder() // JWT를 생성하기 위한 빌더 객체 생성
                .setClaims(claims) // 앞서 생성한 클레임 정보를 JWT 빌더에 설정. JWT에 사용자 이름이 포함되게 함
                .setIssuedAt(new Date(System.currentTimeMillis())) // JWT의 발급 시간을 현재 시간으로 설정
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs)) // JWT의 만료 시간을 현재 시간으로부터 expireTimeMs 밀리초 후로 설정(토큰 유효 기간 설정)
                .signWith(SignatureAlgorithm.HS256, key)// JWT를 서명하기 위해 사용할 알고리즘과 비밀 키 설정
                .compact(); // 최종 JWT 문자열을 생성하고 반환. 이 문자열은 클라이언트에게 전달되고, 클라이언트는 이 JWT를 사용하여 인증 및 권한 부여를 수행할 수 있음
    }
}
