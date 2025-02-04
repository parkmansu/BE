package com.example.seesaw.repository;

import com.example.seesaw.model.Post;
import com.example.seesaw.model.PostScrap;
import com.example.seesaw.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {
    Optional<PostScrap> findByUserAndPost(User user, Post post);
    List<PostScrap> findAllByUserId(Long userId);
    List<PostScrap> findAllByPostId(Long postId);
}
