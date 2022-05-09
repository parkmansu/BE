package com.example.seesaw.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostCommentRequestDto {
    private String nickname;
    private String comment;
}
