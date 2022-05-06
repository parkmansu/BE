package com.example.seesaw.repository;

import com.example.seesaw.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByPostId(Long postid);

}
