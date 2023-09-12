package com.example.springsecurity.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable() // HTTP 기본 인증을 비활성화. 웹 애플리케이션에 기본 인증 사용하지 않도록 설정
                .csrf().disable()  // Cross-Site Requeset Forgery 보호 비활성화. CSRF 공격 방지하는 기능을 끔
                .cors().and() // Cross-Origin Resource Sharing(다른 도메인에서의 요청을 허용 또는 제한하는 보안 기능) 설정 활성화 하고 그 다음 설정을 계속 함
                .authorizeRequests() // 요청에 대한 권한 부여 설정을 시작
                .antMatchers("/api/**").permitAll() // "/api/"로 시작하는 모든 요청을 모든 사용자에게 허용. 인증되지 않은 모든 사용자가 이 경로에 액세스 가능
                .antMatchers("/api/v1/users/join", "/api/v1/users/longin").permitAll() // 이 두 경로에 대해서도 모든 사용자에게 허용
                .and()
                .sessionManagement() // 세션 관리 설정으르 시작
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 생성 정책 설정. 세션을 사용하지 않고 상태가 없는(Stateless) 세션 관리 사용 => RESTful 웹 애플리케이션에서 일반적으로 사용됨
                .and()
//                .addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class) // JWT 토큰을 사용하여 인증 처리하는 데 사용. 'JwtTokenFilter'를 'UsernamePasswordAuthenticationFilter' 앞에 추가
                .build(); // 설정 완료하고 'SecurityFilterChain' 객체를 빌드하여 반환
    }
}
