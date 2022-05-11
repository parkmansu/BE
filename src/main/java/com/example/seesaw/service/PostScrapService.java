package com.example.seesaw.service;

import com.example.seesaw.model.Post;
import com.example.seesaw.model.PostScrap;
import com.example.seesaw.model.User;
import com.example.seesaw.repository.PostRepository;
import com.example.seesaw.repository.PostScrapRepository;
import com.example.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class PostScrapService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostScrapRepository postScrapRepository;

    @Transactional
    public void scrapPost(Long postId) {
        User user = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Error : post is not found"));
        PostScrap postScrap = new PostScrap(user, post);

        // 스크랩 count + 1
        post.setScrapCount(post.getScrapCount()+1);
        postRepository.save(post);

        postScrapRepository.save(postScrap);
    }

    @Transactional
    public void unScrapPost(Long postId) {
        User user = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Error : post is not found"));
        PostScrap postScrap = postScrapRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new RuntimeException("Error : postScrap is not found"));

        // 스크랩 count -1
        post.setScrapCount(post.getScrapCount()-1);
        postRepository.save(post);

        postScrapRepository.delete(postScrap);
    }

    // 현재 사용자 정보(토큰에 있는 정보를 활용?!) //https://00hongjun.github.io/spring-security/securitycontextholder/ 참고.
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("뭘까????~~~~~~~"+username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error : User is not found"));
    }

}
