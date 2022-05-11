package com.example.seesaw.repository;

import com.example.seesaw.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    List<PostComment> findAllByNickname(String nickname);

    List<PostComment> findAllByPostIdOrderByLikeCountDesc(Long postId);

    List<PostComment> findAllByPostId(Long id);
}
