package com.example.seesaw.repository;

import com.example.seesaw.model.GominImage;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface GominImageRepository extends JpaRepository<GominImage, Long> {
    List<GominImage> findAllByGominId(Long gominid);
    @Transactional
    void deleteByGominImageUrl(String lastImageUrl);
    @Transactional
    void deleteAllByGominId(Long gominid);
}
