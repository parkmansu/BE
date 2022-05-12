package com.example.seesaw.dto;

import com.example.seesaw.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostScrapSortResponseDto {

    private Long postId;
    private String title;
    private String contents;
    private String imageUrl;
    private String generation;
    private int scrapCount;

    public PostScrapSortResponseDto(Post post, String imageUrl){
        this.postId = post.getId();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.imageUrl = imageUrl;
        this.generation = post.getGeneration();
        this.scrapCount = post.getScrapCount();
    }

}
