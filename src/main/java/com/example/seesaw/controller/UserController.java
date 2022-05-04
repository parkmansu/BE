package com.example.seesaw.controller;

import com.example.seesaw.dto.RefreshTokenDto;
import com.example.seesaw.dto.SignupRequestDto;
import com.example.seesaw.dto.UserCheckRequestDto;
import com.example.seesaw.model.UserProfile;
import com.example.seesaw.repository.UserProfileRepository;
import com.example.seesaw.dto.MbtiRequestDto;
import com.example.seesaw.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserProfileRepository userProfileRepository;
    private final UserService userService;

    //이메일, 비밀번호 유효성 확인
    @PostMapping("/user/check")
    public ResponseEntity<String> checkUser(@RequestBody UserCheckRequestDto userCheckRequestDto) {
        userService.checkUser(userCheckRequestDto);
        return ResponseEntity.ok()
                .body("이메일, 비밀번호 확인완료");
    }

    //mbti 유효성 확인 후 내용 Return
    @PostMapping("/user/mbti")
    public ResponseEntity<String> checkMbti(@RequestBody MbtiRequestDto mbtiRequestDto) {
        String mbtiDetail = userService.checkMbti(mbtiRequestDto);
        return ResponseEntity.ok()
                .body(mbtiDetail);
    }

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
                .header("Authorization", "Bearer " + accessToken)
                .body("accessToken 재발급 완료");
    }

    //캐릭터 커스터마이징 사진조회
    @GetMapping("/user/profiles")
    public List<UserProfile> findProfiles() {
        //test data
//        UserProfile basicProfile = new UserProfile(1L, "기본이미지", "https://myseesaw.s3.ap-northeast-2.amazonaws.com/basicface.jpg");
//        userProfileRepository.save(basicProfile);
//        UserProfile hairProfile1 = new UserProfile(2L, "머리1", "https://myseesaw.s3.ap-northeast-2.amazonaws.com/hair.jpg");
//        userProfileRepository.save(hairProfile1);
//        UserProfile hatProfile2 = new UserProfile(3L, "모자1", "https://myseesaw.s3.ap-northeast-2.amazonaws.com/hat.jpg");
//        userProfileRepository.save(hatProfile2);
//        UserProfile eyesProfile3 = new UserProfile(4L, "표정1", "https://myseesaw.s3.ap-northeast-2.amazonaws.com/eyes.jpg");
//        userProfileRepository.save(eyesProfile3);

        return userProfileRepository.findAll();
    }

}

