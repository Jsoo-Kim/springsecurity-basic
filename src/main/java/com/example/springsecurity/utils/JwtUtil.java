package com.example.springsecurity.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

    public static String getUserName(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("userName", String.class);
    }

    public static boolean isExpired(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token) // Jwts 클래스를 사용하여 JWT를 파싱(구문 분석)하고 서명을 검증함
                .getBody().getExpiration().before(new Date()); // JWT의 본문을 가져와서 그 안에서 만료 시간을 얻고, 현재 시간과 비교하여 만료되었는지 확 (before()로 검사한 값이 true면 만료된 것)
    }


    // 사용자 이름, 비밀 키, 토큰의 만료시간을 사용하여 JWT(JSON Web Token) 생성
    public static String createToken(String userName, String key, long expiredTimeMs) { // 유저네임은 writeReview 컨트롤러에서 사용자id 등을 따로 입력 받지 않아도 토큰에 들어 있는 유저네임 꺼내서 사용(인증), 시크릿키는 토큰에 서명하는 데 사용
        Claims claims = Jwts.claims(); // 일종의 맵. 사용자 이름 등 원하는 정보를 저장할 곳
        claims.put("userName", userName); // JWT의 클레임에 사용자 이름 추가. 키-값 형태

        return Jwts.builder() // JWT를 생성하기 위한 빌더 객체 생성
                .setClaims(claims) // 앞서 생성한 클레임 정보를 JWT 빌더에 설정. JWT에 사용자 이름이 포함되게 함
                .setIssuedAt(new Date(System.currentTimeMillis())) // JWT의 발급 시간을 현재 시간으로 설정
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs)) // JWT의 만료 시간을 현재 시간으로부터 expiredTimeMs 밀리초 후로 설정(토큰 유효 기간 설정)
                .signWith(SignatureAlgorithm.HS256, key)// JWT를 서명하기 위해(JWT를 변조하지 않도록 보호하는 과정) 사용할 알고리즘과 비밀 키 설정
                .compact(); // 최종 JWT 문자열을 생성하고 반환. 이 문자열은 클라이언트에게 전달되고, 클라이언트는 이 JWT를 사용하여 인증 및 권한 부여를 수행할 수 있음
    }
}
