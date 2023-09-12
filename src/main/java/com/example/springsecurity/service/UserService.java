package com.example.springsecurity.service;

import com.example.springsecurity.domain.User;
import com.example.springsecurity.exception.AppException;
import com.example.springsecurity.exception.ErrorCode;
import com.example.springsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    public String join(String userName, String password) {

        // userName 중복체크
        userRepository.findByUserName(userName)
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, userName + "은/는 이미 존재하는 이름입니다.");
                });

        // 저장
        User user = User.builder()
                .userName(userName)
                .password(encoder.encode(password))
                .build();
        userRepository.save(user);

        return "SUCCESS";
    }
}
