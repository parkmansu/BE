package com.example.seesaw.controller;


import com.example.seesaw.dto.PostRequestDto;
import com.example.seesaw.dto.PostResponseDto;
import com.example.seesaw.model.Post;
import com.example.seesaw.repository.PostRepository;
import com.example.seesaw.security.UserDetailsImpl;
import com.example.seesaw.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;


    //단어 등록
    @PostMapping(value = "/api/post", headers = ("content-type=multipart/*"))
    public PostResponseDto createPost(
            @RequestPart("com") PostRequestDto requestDto,
            @RequestPart("files") ArrayList<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return postService.createPost(requestDto, files, userDetails.getUser());
    }


    // 단어 중복 확인
    @GetMapping("/api/post/{title}/exists")
    public ResponseEntity<Boolean> wordCheck(@PathVariable String title) {
        return ResponseEntity.ok(postService.wordCheck(title));
    }

    //단어 수정!
    @PutMapping("/api/post/{postId}/update")
    public PostResponseDto updatePost(
            @PathVariable Long postId,
            @RequestPart(value="file",required = false)  PostRequestDto requestDto,
            @RequestPart ArrayList<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return postService.updatePost(postId, requestDto, files, userDetails.getUser());
    }


    // 검색
    @GetMapping("/api/post/search")
    public List<Post> search(String keyword) {
        return postRepository.findByTitleContainingOrContentsContaining(keyword, keyword);
    }

}
