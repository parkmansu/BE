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
    private int scrapCount;

    public PostScrapSortResponseDto(Post post){
        this.postId = post.getId();
        this.title = post.getTitle();
        this.scrapCount = post.getScrapCount();
    }

}
