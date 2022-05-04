package com.example.seesaw.dto;

import com.example.seesaw.model.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseDto {
    private String username;
    private String nickname;
    private List<UserProfile> profileImage;
}
