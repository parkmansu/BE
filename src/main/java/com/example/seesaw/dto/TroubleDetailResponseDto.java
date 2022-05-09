package com.example.seesaw.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TroubleDetailResponseDto {
    private String nickname;
    private List<ProfileListDto> profileImages;
    private String title;
    private String contents;
    private String question;
    private String answer;
    private List<String> tagName;
    private List<String> imageUrls;
    private Long views;
    private Long commentCount;
    private String postTime;
    private List<TroubleCommentRequestDto> troubleComments;

    public TroubleDetailResponseDto(TroubleResponseDto troubleResponseDto) {
        this.title = troubleResponseDto.getTitle();
        this.contents = troubleResponseDto.getContents();
        this.question = troubleResponseDto.getQuestion();
        this.answer = troubleResponseDto.getAnswer();
        this.tagName = troubleResponseDto.getTagName();
        this.imageUrls = troubleResponseDto.getImageUrls();
    }
}
