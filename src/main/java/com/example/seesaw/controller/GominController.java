package com.example.seesaw.controller;

import com.example.seesaw.dto.GominRequestDto;
import com.example.seesaw.dto.GominResponseDto;
import com.example.seesaw.repository.GominRepository;
import com.example.seesaw.security.UserDetailsImpl;
import com.example.seesaw.service.GominService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GominController {
    private final GominService gominService;
    private final GominRepository gominRepository;

    //고민글 등재
    @PostMapping("/api/gomin")
    public ResponseEntity<String> registerGomin(
            @RequestPart(value = "gominRequestDto") GominRequestDto gominRequestDto,
            @RequestPart(value = "files") List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        gominService.registerGomin(gominRequestDto, files, userDetails.getUser());
        return ResponseEntity.ok()
                .body("고민글 등재완료");
    }

    //고민글 수정 시 고민글 조회
    @GetMapping("api/gomin/{gominid}")
    public ResponseEntity<GominResponseDto> updateGomin(@PathVariable Long gominid){
        GominResponseDto gominResponseDto = gominService.findGomin(gominid);
        return ResponseEntity.ok()
                .body(gominResponseDto);
    }
    //고민글 수정
    @PutMapping("api/gomin/{gominid}")
    public ResponseEntity<String> updateGomin(
            @RequestPart(value = "gominRequestDto") GominRequestDto gominRequestDto,
            @RequestPart(value = "files") List<MultipartFile> files,
            @PathVariable Long gominid,
            @AuthenticationPrincipal UserDetailsImpl userDetails){

        gominService.updateGomin(gominRequestDto, files, gominid, userDetails.getUser());
        return ResponseEntity.ok()
                .body("고민글 수정완료");
    }
    //고민글 삭제
    @DeleteMapping("api/gomin/{gominid}")
    public ResponseEntity<String> deleteGomin(@PathVariable Long gominid){
        gominRepository.deleteById(gominid);
        return ResponseEntity.ok()
                .body("고민글 삭제완료");
    }
}
