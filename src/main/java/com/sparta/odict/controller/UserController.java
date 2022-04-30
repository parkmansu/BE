package com.sparta.odict.controller;

import com.sparta.odict.dto.RefreshTokenDto;
import com.sparta.odict.dto.SignupRequestDto;
import com.sparta.odict.model.User;
import com.sparta.odict.security.UserDetailsImpl;
import com.sparta.odict.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/user/signup")
    public ResponseEntity<String> registerUser(@RequestBody SignupRequestDto requestDto) {
        userService.registerUser(requestDto);
        return ResponseEntity.ok()
                .body("회원가입 완료");
    }

    //accessToken 만료 시 refreshToken 유효한지 확인 후 accessToken 재발급
    @PostMapping("/user/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        String accessToken = userService.validateToken(refreshTokenDto);
        return ResponseEntity.ok()
                .header("accessToken", accessToken)
                .body("accessToken 재발급 완료");
    }
}

