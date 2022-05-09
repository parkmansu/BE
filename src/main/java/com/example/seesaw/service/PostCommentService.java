package com.example.seesaw.service;

import com.example.seesaw.dto.PostCommentRequestDto;
import com.example.seesaw.model.Post;
import com.example.seesaw.model.PostComment;
import com.example.seesaw.model.User;
import com.example.seesaw.repository.PostCommentRepository;
import com.example.seesaw.repository.PostRepository;
import com.example.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;

    public void registerPostComment(Long postId, PostCommentRequestDto requestDto, User user) {
        User commentUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalStateException("해당하는 USER 가 없습니다.")
        );
        requestDto.setNickname(commentUser.getNickname());
        Post savedPost = postRepository.findById(postId).orElseThrow(
                () -> new IllegalStateException("해당 게시글이 없습니다."));
        PostComment postComment = new PostComment(savedPost, requestDto);
        postCommentRepository.save(postComment);
    }

    public void updatePostComment(Long commentId, PostCommentRequestDto requestDto, User user) {
        PostComment postComment = checkCommentUser(commentId, user);
        postComment.setNickname(user.getNickname());
        postComment.setComment(requestDto.getComment());
        postCommentRepository.save(postComment);
    }

    public void deletePostComment(Long commentId, User user) {
        checkCommentUser(commentId, user);
        postCommentRepository.deleteById(commentId);
    }

    public PostComment checkCommentUser(Long commentId, User user){
        User commentUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalStateException("해당하는 USER 가 없습니다.")
        );
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(
                () -> new IllegalStateException("해당 댓글이 없습니다."));
        if(!commentUser.getNickname().equals(postComment.getNickname())){
            throw new IllegalArgumentException("댓글 작성자가 아니므로 댓글 수정, 삭제가 불가합니다.");
        }
        return postComment;
    }

}
