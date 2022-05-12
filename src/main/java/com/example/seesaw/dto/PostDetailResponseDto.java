package com.example.seesaw.dto;

import com.example.seesaw.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResponseDto {

    private String LastNickname;
    private List<ProfileListDto> profileImages;
    private String title;
    private String contents;
    private List<String> tagName;
    private List<String> postImages;
    private String generation;
    private String videoUrl;
    private Long views;
    private Long commentCount;
    private String postUpdateTime;
    private List<PostCommentRequestDto> postComments;
    private int scrapCount;
    private String nickname;



    public PostDetailResponseDto(PostResponseDto responseDto) {
        this.tagName = responseDto.getTagNames();
        this.postImages = responseDto.getPostImages();
    }

}
