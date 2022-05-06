package com.example.seesaw.repository;

import com.example.seesaw.model.Gomin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GominRepository extends JpaRepository<Gomin, Long> {
    List<Gomin> findAllByUserId(Long id);
}
