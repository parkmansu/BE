package com.example.seesaw.dto;

import com.example.seesaw.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class PostResponseDto {
    private String title;
    private String contents;
    private List<String> postImages;
    private List<String> tagNames;
    private String videoUrl;
    private String generation;
    private String user;

    public PostResponseDto(Post post, List<String> postImages, List<String> tagNames) {
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.postImages = postImages;
        this.videoUrl = post.getVideoUrl();
        this.tagNames = tagNames;
        this.generation = post.getGeneration();
        this.user = post.getUser().getNickname();
    }
}
