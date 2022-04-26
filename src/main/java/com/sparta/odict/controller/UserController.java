package com.sparta.odict.controller;

import com.sparta.odict.dto.SignupRequestDto;
import com.sparta.odict.security.jwt.JwtTokenUtils;
import com.sparta.odict.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class
UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/user/signup")
    public ResponseEntity<String> registerUser(@RequestBody SignupRequestDto requestDto) {
        userService.registerUser(requestDto);
        return ResponseEntity.ok()
                .body("회원가입 완료");
    }
}
