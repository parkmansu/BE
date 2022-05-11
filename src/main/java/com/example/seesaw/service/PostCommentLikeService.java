package com.example.seesaw.service;

import com.example.seesaw.model.PostComment;
import com.example.seesaw.model.PostCommentLike;
import com.example.seesaw.model.User;
import com.example.seesaw.repository.PostCommentLikeRepository;
import com.example.seesaw.repository.PostCommentRepository;
import com.example.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class PostCommentLikeService {

    private final PostCommentRepository postCommentRepository;
    private final PostCommentLikeRepository postCommentLikeRepository;
    private final UserRepository userRepository;


    // 예시 좋아요
    @Transactional
    public void likeComment(Long commentId) {
        User user = getCurrentUser();
        PostComment postComment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Error : postComment is not found"));
        PostCommentLike postCommentLike = new PostCommentLike(postComment, user);
        postCommentLikeRepository.save(postCommentLike);
        postComment.setLikeCount(postComment.getLikeCount()+1);
    }
    // 예시 좋아요 취소
    @Transactional
    public void unlikeComment(Long commentId) {
        User user = getCurrentUser();
        PostComment postComment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Error : comment is not found"));
        PostCommentLike postCommentLike = postCommentLikeRepository.findByUserAndPostComment(user, postComment)
                .orElseThrow(() -> new RuntimeException("Error : commentLike is not found"));
        postCommentLikeRepository.delete(postCommentLike);
        postComment.setLikeCount(postComment.getLikeCount()-1);
    }

    // 현재 사용자 정보(토큰에 있는 정보를 활용?!) //https://00hongjun.github.io/spring-security/securitycontextholder/ 참고.
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error : User is not found"));
    }

}
