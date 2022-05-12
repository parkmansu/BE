package com.example.seesaw.dto;

import com.example.seesaw.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostListResponseDto {

    private Long postId;
    private String title;
    private String contents;
    private String generation;
    private Long views;
    private int scrapCount;
    private List<String> postImages;

    public PostListResponseDto(Post post){
        this.postId = post.getId();
        this.title = post.getTitle();
        this.scrapCount = post.getScrapCount();
        this.contents = post.getContents();
        this.generation = post.getGeneration();
        this.views = post.getViews();
    }

}
