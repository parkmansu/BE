package com.example.seesaw.service;

import com.example.seesaw.dto.*;
import com.example.seesaw.model.*;
import com.example.seesaw.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TroubleService {

    private final TroubleTagRepository troubleTagRepository;
    private final TroubleRepository troubleRepository;
    private final TroubleImageRepository troubleImageRepository;
    private final TroubleS3Service troubleS3Service;
    private final TroubleCommentRepository troubleCommentRepository;
    private final ConvertTimeService convertTimeService;
    private final UserService userService;
    private final UserRepository userRepository;

    //Trouble 글 등록
    public void registerTrouble(TroubleRequestDto troubleRequestDto, List<MultipartFile> files, User user) {
        checkTrouble(troubleRequestDto);
        String name = null;
        for(MultipartFile file:files){
            name = file.getOriginalFilename();
            System.out.println("file이름은~~~:" + name);
        }

        List<String> imagePaths = new ArrayList<>();
        if (name.equals("")) {
            imagePaths.add("기본이미지 AWS에 저장해서 주소넣기!");
        } else {
            imagePaths.addAll(troubleS3Service.upload(files));
        }
        Trouble trouble = new Trouble(
                troubleRequestDto.getTitle(),
                troubleRequestDto.getContents(),
                troubleRequestDto.getQuestion(),
                troubleRequestDto.getAnswer(),
                0L,
                user
        );
        troubleRepository.save(trouble);

        for (String imagePath : imagePaths) {
            TroubleImage troubleImage = new TroubleImage(imagePath, user, trouble);
            troubleImageRepository.save(troubleImage);
        }

        List<String> tags = troubleRequestDto.getTagName();
        for (String tag : tags) {
            if(!tag.equals("")){
                TroubleTag troubleTag = new TroubleTag(tag, user, trouble);
                troubleTagRepository.save(troubleTag);
            }
        }
    }

    //고민글 수정 시 정보조회
    public TroubleResponseDto findTrouble(Long troubleId) {
        Trouble trouble = troubleRepository.findById(troubleId).orElseThrow(
                () -> new IllegalArgumentException("고민 Id에 해당하는 글이 없습니다.")
        );


        List<TroubleTag> troubleTags = troubleTagRepository.findAllByTroubleId(troubleId);
        if (troubleTags.isEmpty()){
            throw new IllegalArgumentException("고민 Id에 해당하는 테그가 없습니다.");
        }
        List<String> gomiTagList = new ArrayList<>();
        for(TroubleTag troubleTag : troubleTags){
            gomiTagList.add(troubleTag.getTagName());
        }

        List<TroubleImage> troubleImages = troubleImageRepository.findAllByTroubleId(troubleId);
        if (troubleImages.isEmpty()){
            throw new IllegalArgumentException("고민 Id에 해당하는 이미지가 없습니다.");
        }
        List<String> troubleImageList = new ArrayList<>();
        for(TroubleImage troubleImage : troubleImages){
            troubleImageList.add(troubleImage.getTroubleImageUrl());
        }

        return new TroubleResponseDto(trouble.getTitle(), trouble.getContent(), trouble.getQuestion(), trouble.getAnswer(), gomiTagList, troubleImageList);
    }

    //고민글 수정
    public void updateTrouble(TroubleRequestDto troubleRequestDto, List<MultipartFile> files, Long troubleId, User user) {
        Trouble trouble = troubleRepository.findById(troubleId).orElseThrow(
                () -> new IllegalArgumentException("고민 Id에 해당하는 고민글이 없습니다.")
        );
        //고민글 작성자 검사
        Long troubleUserId = trouble.getUser().getId();
        if(!user.getId().equals(troubleUserId)){
            throw new IllegalArgumentException("작성자가 아니므로 고민글 수정이 불가합니다.");
        }
        checkTrouble(troubleRequestDto);
        String name = null;
        for(MultipartFile file:files){
            name = file.getOriginalFilename();
            System.out.println("file이름은~~~:" + name);
        }
        trouble.update(troubleRequestDto);

        List<String> imagePaths = new ArrayList<>();
        if (name.equals("") && troubleRequestDto.getImageUrls().get(0).isEmpty()) {
            imagePaths.add("기본이미지 AWS에 저장해서 주소넣기!");
            troubleS3Service.delete(troubleId, troubleRequestDto.getImageUrls());
            troubleImageRepository.deleteAllByTroubleId(troubleId);
        } else if(!name.equals("")) {
            imagePaths.addAll(troubleS3Service.update(troubleId, troubleRequestDto.getImageUrls(), files));
        } else{
            imagePaths = troubleRequestDto.getImageUrls();
            troubleS3Service.delete(troubleId, troubleRequestDto.getImageUrls());
            troubleImageRepository.deleteAllByTroubleId(troubleId);
        }

        for (String imagePath : imagePaths) {
            TroubleImage troubleImage = new TroubleImage(imagePath, user, trouble);
            troubleImageRepository.save(troubleImage);
        }
        List<String> tags = troubleRequestDto.getTagName();
        troubleTagRepository.deleteAllByTroubleId(troubleId);
        for (String tag : tags) {
            if(!tag.equals("")){
                TroubleTag troubleTag = new TroubleTag(tag, user, trouble);
                troubleTagRepository.save(troubleTag);
            }
        }
    }

    //고민글 유효성 검사
    public void checkTrouble(TroubleRequestDto troubleRequestDto) {
        if (troubleRequestDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("제목입력은 필수값입니다.");
        } else if (troubleRequestDto.getContents().isEmpty()) {
            throw new IllegalArgumentException("내용입력은 필수값입니다.");
        } else if (troubleRequestDto.getQuestion().isEmpty()) {
            throw new IllegalArgumentException("질문자세대 입력은 필수값입니다.");
        } else if (troubleRequestDto.getAnswer().isEmpty()) {
            throw new IllegalArgumentException("답변자세대 입력은 필수값입니다.");
        }
    }

    public TroubleDetailResponseDto findDetailTrouble(Long troubleId) {
        Trouble trouble = troubleRepository.findById(troubleId).orElseThrow(
                () -> new IllegalArgumentException("고민 Id에 해당하는 글이 없습니다.")
        );
        TroubleResponseDto troubleResponseDto = findTrouble(troubleId);

        TroubleDetailResponseDto troubleDetailResponseDto = new TroubleDetailResponseDto(troubleResponseDto);
        troubleDetailResponseDto.setNickname(trouble.getUser().getNickname());
        troubleDetailResponseDto.setProfileImages(userService.findUserProfiles(trouble.getUser()));
        String postTime = convertTimeService.convertLocaldatetimeToTime(trouble.getCreatedAt());
        troubleDetailResponseDto.setPostTime(postTime);
        troubleDetailResponseDto.setViews(trouble.getViews());
        trouble.setViews(trouble.getViews()+1);
        troubleRepository.save(trouble);

        List<TroubleComment> troubleComments = troubleCommentRepository.findAllByTroubleIdOrderByLikeCountDesc(troubleId);
        troubleDetailResponseDto.setCommentCount((long) troubleComments.size());
        List<TroubleCommentRequestDto> troubleCommentRequestDtos = new ArrayList<>();
        for(TroubleComment troubleComment:troubleComments){
            TroubleCommentRequestDto troubleCommentRequestDto = new TroubleCommentRequestDto(troubleComment);
            User user = userRepository.findByNickname(troubleComment.getNickname()).orElseThrow(
                    () -> new IllegalArgumentException("고민댓글에 해당하는 사용자를 찾을 수 없습니다."));
            troubleCommentRequestDto.setProfileImages(userService.findUserProfiles(user));
            troubleCommentRequestDtos.add(troubleCommentRequestDto);
        }
        troubleDetailResponseDto.setTroubleComments(troubleCommentRequestDtos);

        return troubleDetailResponseDto;
    }

    public List<TroubleAllResponseDto> findAllTroubles() {
        List<Trouble> troubles = troubleRepository.findAllByOrderByCreatedAtDesc();
        return getTroubles(troubles);
    }

    public List<TroubleAllResponseDto> findViewTroubles() {
        List<Trouble> troubles = troubleRepository.findAllByOrderByViewsDesc();
        return getTroubles(troubles);
    }

    public List<TroubleAllResponseDto> getTroubles(List<Trouble> troubles){
        if(troubles.isEmpty()){
            throw new IllegalArgumentException("작성된 고민글이 없습니다.");
        }
        List<TroubleAllResponseDto> troubleAllResponseDtos = new ArrayList<>();
        long id = 0L;
        for(Trouble trouble:troubles){
            TroubleResponseDto troubleResponseDto = findTrouble(trouble.getId());
            TroubleAllResponseDto troubleAllResponseDto = new TroubleAllResponseDto(troubleResponseDto);
            troubleAllResponseDto.setId(++id);
            troubleAllResponseDto.setNickname(trouble.getUser().getNickname());
            troubleAllResponseDto.setProfileImages(userService.findUserProfiles(trouble.getUser()));
            String postTime = convertTimeService.convertLocaldatetimeToTime(trouble.getCreatedAt());
            troubleAllResponseDto.setPostTime(postTime);
            troubleAllResponseDto.setViews(trouble.getViews());
            List<TroubleComment> troubleComments = troubleCommentRepository.findAllByTroubleId(trouble.getId());
            troubleAllResponseDto.setCommentCount((long) troubleComments.size());
            troubleAllResponseDtos.add(troubleAllResponseDto);
        }
        return troubleAllResponseDtos;
    }
}
