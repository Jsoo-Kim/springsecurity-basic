package com.example.springsecurity.configuration;

import com.example.springsecurity.service.UserService;
import com.example.springsecurity.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// 1. 요청 헤더에서 JWT 토큰을 추출
// 2.JWT 토큰이 유효한지 확인
// 3. JWT 토큰에서 사용자 이름을 추출
// 4. 사용자 이름을 사용하여 인증 토큰을 생성
// 5. 인증 토큰을 SecurityContextHolder에 저장
// 6. 요청을 다음 필터에 전달
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter { // 웹 애플리케이션의 인증(Authentication)과 권한 부여(Authorization)를 처리하는 필터를 정의

    private final UserService userService;
    private final String secretKey;

    // 통과시키는 관문이라고 생각하면 됨
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 현재 요청의 Authorization 헤더 값을 가져와서 authorization 변수에 할당 -> 이후 해당 인증 정보 검사하거나 처리하는 데 사용
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization : {}", authorization);

        // 토큰 안 보내면 Block -> 회원가입, 로그인은 제외해줘야 하는데 방법 없나 => 토큰 인증이 필요한 컨트롤러나 엔드포인트에서 직접 에러 처리!
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("Authorization이 없거나 잘못된 값입니다.");
            filterChain.doFilter(request, response); // HTTP 요청 및 응답을 필터링하고 처리, 현재 필터에서 처리한 요청과 응답을 다음 필터 또는 서블릿으로 전달
            return;
        }

        // 위 if문은 검증이 실패한 경우인데 filterChain.doFilter(request, response); 이 코드가 왜 필요하지? 요청을 다음 필터에 전달할 필요가 있나?
        // => 1. 요청에 문제가 있는지 확인하기 위해 다른 필터에서 추가 검사를 수행할 수 있음 / 2. 요청에 문제가 있더라도 다른 필터를 통해 처리될 수 있는 경우가 있음
        // ex) 토큰 검증이 실패한 경우에도 요청에 대한 로그를 기록하는 필터가 있을 수 있음 / 토큰 검증이 실패한 경우에도 요청에 대한 에러 페이지를 표시하는 필터가 있을 수 있음

        // 토큰 꺼내기
        String token = authorization.split(" ")[1]; // Bearer를 빼 준 값

        // 토큰 만료(Expired) 여부
        if (JwtUtil.isExpired(token, secretKey)) {
            log.error("Token이 만료되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // userName 토큰에서 꺼내기
        String userName = JwtUtil.getUserName(token, secretKey);
        log.info("userName: {}", userName); // 여기서 userName 꺼내서 아래 권한 부여에 넣어주면 컨트롤러에서 사용 가능

        // 권한 부여
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("USER"))); // 인증된 사용자를 나타내는 객체 생성. 사용자 이름(고유 식별자), 암호, 부여된 권한의 목록(USER 권한은 기본 작업과 같은 리소스 보기 및 생성)
        // Detail 넣어주기
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 위 객체의 detail 속성 설정. 인증에 대한 추가 정보(ex 클라이언트의 IP)
        SecurityContextHolder.getContext().setAuthentication(authenticationToken); // 현재 스레드의 인증 컨텍스트(인증된 사용자를 저장하는 홀더) 설정. 인증된 사용자가 다른 구성 요소(ex 컨트롤러)를 사용할 수 있게 함
        filterChain.doFilter(request, response); // 요청과 응답 객체를 필터 체인의 다음 필터에 전달 -> 요청이 필터 체인의 모든 필터에서 처리되도록 함
    }
}
