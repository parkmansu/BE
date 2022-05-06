package com.example.seesaw.repository;

import com.example.seesaw.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByPostId(Long postId);

    void deleteAllById(Long postId);
}
