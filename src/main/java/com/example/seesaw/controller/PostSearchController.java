package com.example.seesaw.controller;

import com.example.seesaw.model.Post;
import com.example.seesaw.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class PostSearchController {

    private final PostRepository postRepository;

    // 검색
    @GetMapping("/api/post/search")
    public List<Post> search(String keyword) {
        return postRepository.findByTitleContainingOrContentsContaining(keyword, keyword);
    }
}
