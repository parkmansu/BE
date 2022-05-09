package com.example.seesaw.service;

import com.example.seesaw.dto.ProfileListDto;
import com.example.seesaw.dto.ProfileRequestDto;
import com.example.seesaw.dto.ProfileResponseDto;
import com.example.seesaw.model.TroubleComment;
import com.example.seesaw.model.UserProfile;
import com.example.seesaw.model.UserProfileNum;
import com.example.seesaw.repository.TroubleCommentRepository;
import com.example.seesaw.repository.UserProfileNumRepository;
import com.example.seesaw.repository.UserProfileRepository;
import com.example.seesaw.model.User;
import com.example.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPageService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileNumRepository userProfileNumRepository;
    private final TroubleCommentRepository troubleCommentRepository;

    public void updateProfile(ProfileRequestDto profileRequestDto, User user) {
        //닉네임 유효성 검사 후 저장
        String nickname = userService.checkNickName(profileRequestDto.getNickname());
        //고민댓글 nickname 변경
        List<TroubleComment> troubleComments = troubleCommentRepository.findAllByNickname(user.getNickname());
        for(TroubleComment troubleComment:troubleComments){
            troubleComment.setNickname(profileRequestDto.getNickname());
            troubleCommentRepository.save(troubleComment);
        }
//        List<PostComment> postComments = postCommentRepository.findAllByNickname(user.getNickname());
//        for(PostComment postComment:postComments){
//            postComment.setNickname(profileRequestDto.getNickname());
//            postCommentRepository.save(postComment);
//        }
        user.setNickname(nickname);
        userRepository.save(user);

        //IDs 유효성 검사 후 IDs 저장
        List<Long> profileImageCharIds = profileRequestDto.getProfileImages();
        List<UserProfileNum> userProfileNums = new ArrayList<>();
        for(Long charId : profileImageCharIds){
            UserProfile userProfile = userProfileRepository.findByCharId(charId);
            UserProfileNum userProfileNum = new UserProfileNum(userProfile, user);
            userProfileNums.add(userProfileNum);
        }
        userProfileNumRepository.deleteAllByUserId(user.getId());
        userProfileNumRepository.saveAll(userProfileNums);
    }

    public ProfileResponseDto findProfiles() {
        List<ProfileListDto> faceUrl = new ArrayList<>();
        List<ProfileListDto> accessoryUrl = new ArrayList<>();
        List<ProfileListDto> backgroudUrl = new ArrayList<>();
        List<UserProfile> userProfile = userProfileRepository.findAll();
        for(UserProfile profile:userProfile){
            if(profile.getCategory().equals("faceUrl")){
                faceUrl.add(new ProfileListDto(profile.getCharId(), profile.getImageUrl()));
            } else if(profile.getCategory().equals("accessoryUrl")){
                accessoryUrl.add(new ProfileListDto(profile.getCharId(), profile.getImageUrl()));
            } else if(profile.getCategory().equals("backgroundUrl")){
                backgroudUrl.add(new ProfileListDto(profile.getCharId(), profile.getImageUrl()));
            }
        }
        return new ProfileResponseDto(faceUrl, accessoryUrl, backgroudUrl);
    }
}
