package com.example.seesaw.service;

import com.example.seesaw.dto.CrossWordResponseDto;
import com.example.seesaw.model.Post;
import com.example.seesaw.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class CrosswordService {

    private final PostRepository postRepository;

    public List<CrossWordResponseDto> getWord(){
        Random random = new Random(); // 랜덤 객체 생성
        random.setSeed(System.currentTimeMillis());
        // 1부터 3까지 postId 난수 생성
//        Long postId = Long.valueOf(random.nextInt(2) + 1); // 현재 post가 3개밖에 등록이 안되어 있으니까
        Long postId = 8L;
        int x = 1; int y = 3;

        List<Post> posts = postRepository.findAllById(postId);
        // 네글자라고 가정, 가로로만 간다고 가정
        List<CrossWordResponseDto> crossWordResponseDtos = new ArrayList<>();
        for (Post post: posts
             ) {
            String[] str = post.getTitle().split("");
            for (int i = 0; i < str.length; i++){
                crossWordResponseDtos.add(new CrossWordResponseDto(x,y,str[i]));
            }
        }

        return crossWordResponseDtos;
    }
}
