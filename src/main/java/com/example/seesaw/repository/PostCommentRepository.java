package com.example.seesaw.repository;

import com.example.seesaw.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {


}
