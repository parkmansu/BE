package com.example.seesaw.repository;

import com.example.seesaw.model.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    @Transactional
    void deleteAllByPostId(Long postId);

    @Transactional
    void deleteByPostImages(String postImages);

    List<PostImage> findAllByPostId(Long postId);
}
