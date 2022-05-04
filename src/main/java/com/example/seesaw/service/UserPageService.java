package com.example.seesaw.service;

import com.example.seesaw.dto.ProfileRequestDto;
import com.example.seesaw.model.UserProfile;
import com.example.seesaw.model.UserProfileNum;
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

//    public void checkProfile(ProfileRequestDto profileRequestDto) {
//        //닉네임 유효성 검사
//        checkNickName(profileRequestDto.getNickname());
//        //IDs 유효성 검사
//        List<Long> profileImageNums = profileRequestDto.getProfileImages();
//        for(Long num : profileImageNums){
//            userProfileRepository.findById(num).orElseThrow(
//                    () -> new IllegalArgumentException("해당하는 이미지가 없습니다.")
//            );
//            //UserProfileNum userProfileNum = new UserProfileNum(userProfile, user);
//            //userProfileNumRepository.save(userProfileNum);
//        }
//    }

    public void updateProfile(ProfileRequestDto profileRequestDto, User user) {
        //닉네임 유효성 검사 후 저장
        String nickname = userService.checkNickName(profileRequestDto.getNickname());
        user.setNickname(nickname);
        userRepository.save(user);
        //IDs 유효성 검사 후 IDs 저장
        List<Long> profileImageNums = profileRequestDto.getProfileImages();
        List<UserProfileNum> userProfileNums = new ArrayList<>();
        for(Long num : profileImageNums){
            UserProfile userProfile = userProfileRepository.findById(num).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 이미지가 없습니다.")
            );
            UserProfileNum userProfileNum = new UserProfileNum(userProfile, user);
            userProfileNums.add(userProfileNum);
        }
        userProfileNumRepository.deleteAllByUserId(user.getId()); //id 값은 그대로하고 내용만 수정되게 수정하기
        userProfileNumRepository.saveAll(userProfileNums);
    }

//    public GominResponseDto findGomins(User user) {
//        List<Gomin> gomins = gominRepository.findAllByUserId(user.getId());
//        return new GominResponseDto((long) gomins.size(), gomins);
//    }
}
