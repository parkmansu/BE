package com.example.seesaw.repository;

import com.example.seesaw.model.GominImage;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface GominImageRepository extends JpaRepository<GominImage, Long> {
    List<GominImage> findAllByGominId(Long gominid);
    @Transactional
    void deleteByImageUrl(String lastImage);
    @Transactional
    void deleteAllByGominId(Long gominid);
}
