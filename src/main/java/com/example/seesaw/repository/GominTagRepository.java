package com.example.seesaw.repository;

import com.example.seesaw.model.GominTag;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface GominTagRepository extends JpaRepository<GominTag, Long> {
    List<GominTag> findAllByGominId(Long gominid);
    @Transactional
    void deleteAllByGominId(Long gominId);
}
