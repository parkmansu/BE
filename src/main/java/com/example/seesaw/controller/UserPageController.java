package com.example.seesaw.controller;

import com.example.seesaw.dto.UserTroubleResponseDto;
import com.example.seesaw.service.TroubleService;
import com.example.seesaw.dto.ProfileRequestDto;
import com.example.seesaw.dto.UserInfoResponseDto;
import com.example.seesaw.security.UserDetailsImpl;
import com.example.seesaw.service.UserPageService;
import com.example.seesaw.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserPageController {

    private final UserPageService userPageService;
    private final UserService userService;
    private final TroubleService troubleService;

    //내정보 조회
    @GetMapping("/api/mypage")
    public ResponseEntity<UserInfoResponseDto> findMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserInfoResponseDto userInfoResponseDto = userService.findUserInfo(userDetails.getUser());
        return ResponseEntity.ok()
                .body(userInfoResponseDto);
    }
    //내가 작성한 고민글 조회
    @GetMapping("/api/mypage/trouble")
    public ResponseEntity<UserTroubleResponseDto> findTroubles(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserTroubleResponseDto gominResponseDto = troubleService.findTroubles(userDetails.getUser());
        return ResponseEntity.ok()
                .body(gominResponseDto);
    }
    //닉네임, 프로필 이미지 수정
    @PutMapping("/api/mypage/profile")
    public ResponseEntity<String> updateProfile(
            @RequestBody ProfileRequestDto profileRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        userPageService.updateProfile(profileRequestDto, userDetails.getUser());
        return ResponseEntity.ok()
                .body("닉네임, 프로필 이미지 수정 완료");
    }
}
