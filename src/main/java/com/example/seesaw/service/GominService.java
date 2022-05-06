package com.example.seesaw.service;

import com.example.seesaw.dto.GominRequestDto;
import com.example.seesaw.dto.GominResponseDto;
import com.example.seesaw.dto.UserGominResponseDto;
import com.example.seesaw.model.Gomin;
import com.example.seesaw.model.GominImage;
import com.example.seesaw.model.GominTag;
import com.example.seesaw.model.User;
import com.example.seesaw.repository.GominImageRepository;
import com.example.seesaw.repository.GominRepository;
import com.example.seesaw.repository.GominTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GominService {

    private final GominTagRepository gominTagRepository;
    private final GominRepository gominRepository;
    private final GominImageRepository gominImageRepository;
    private final S3Service s3Service;


//    @Override
//    public String toString(){
//        return name;
//    }

    //Gomin 글 등록
    public void registerGomin(GominRequestDto gominRequestDto, List<MultipartFile> files, User user) {
        checkGomin(gominRequestDto);
        String name = null;
        for(MultipartFile file:files){
            name = file.getOriginalFilename();
            System.out.println("file이름은~~~:" + name);
        }

        List<String> imagePaths = new ArrayList<>();
        if (name.equals("")) {
            imagePaths.add("기본이미지 AWS에 저장해서 주소넣기!");
        } else {
            imagePaths.addAll(s3Service.upload(files));
        }
        Gomin gomin = new Gomin(
                gominRequestDto.getTitle(),
                gominRequestDto.getContents(),
                gominRequestDto.getQuestion(),
                gominRequestDto.getAnswer(),
                0L,
                user
        );
        gominRepository.save(gomin);

        for (String imagePath : imagePaths) {
            GominImage gominImage = new GominImage(imagePath, user, gomin);
            gominImageRepository.save(gominImage);
        }

        List<String> tags = gominRequestDto.getTagName();
        for (String tag : tags) {
            if(!tag.equals("")){
                GominTag gominTag = new GominTag(tag, user, gomin);
                gominTagRepository.save(gominTag);
            }
        }
    }

    //고민글 수정 시 정보조회
    public GominResponseDto findGomin(Long gominid) {
        Gomin gomin = gominRepository.findById(gominid).orElseThrow(
                () -> new IllegalArgumentException("고민 id에 해당하는 글이 없습니다.")
        );


        List<GominTag> gominTags = gominTagRepository.findAllByGominId(gominid);
        if (gominTags.isEmpty()){
            throw new IllegalArgumentException("고민 id에 해당하는 테그가 없습니다.");
        }
        List<String> gomiTagList = new ArrayList<>();
        for(GominTag gominTag : gominTags){
            gomiTagList.add(gominTag.getTagName());
        }

        List<GominImage> gominImages = gominImageRepository.findAllByGominId(gominid);
        if (gominImages.isEmpty()){
            throw new IllegalArgumentException("고민 id에 해당하는 이미지가 없습니다.");
        }
        List<String> gominImageList = new ArrayList<>();
        for(GominImage gominImage : gominImages){
            gominImageList.add(gominImage.getImageUrl());
        }

        return new GominResponseDto(gomin.getTitle(), gomin.getContent(), gomin.getQuestion(), gomin.getAnswer(), gomiTagList, gominImageList);
    }

    //고민글 수정
    public void updateGomin(GominRequestDto gominRequestDto, List<MultipartFile> files, Long gominid, User user) {
        Gomin gomin = gominRepository.findById(gominid).orElseThrow(
                () -> new IllegalArgumentException("고민 id에 해당하는 고민글이 없습니다.")
        );
        //고민글 작성자 검사
        Long gominUserId = gomin.getUser().getId();
        if(!user.getId().equals(gominUserId)){
            throw new IllegalArgumentException("작성자가 아니므로 고민글 수정이 불가합니다.");
        }
        checkGomin(gominRequestDto);
        String name = null;
        for(MultipartFile file:files){
            name = file.getOriginalFilename();
            System.out.println("file이름은~~~:" + name);
        }
        gomin.update(gominRequestDto);

        List<String> imagePaths = new ArrayList<>();
        if (name.equals("") && gominRequestDto.getImageUrls().get(0).isEmpty()) {
            imagePaths.add("기본이미지 AWS에 저장해서 주소넣기!");
            gominImageRepository.deleteAllByGominId(gominid);
        } else {
            imagePaths.addAll(s3Service.update(gominid, gominRequestDto.getImageUrls(), files));
        }

        for (String imagePath : imagePaths) {
            GominImage gominImage = new GominImage(imagePath, user, gomin);
            gominImageRepository.save(gominImage);
        }
        List<String> tags = gominRequestDto.getTagName();
        gominTagRepository.deleteAllByGominId(gominid);
        for (String tag : tags) {
            if(!tag.equals("")){
                GominTag gominTag = new GominTag(tag, user, gomin);
                gominTagRepository.save(gominTag);
            }
        }
    }

    //USER가 작성한 고민리스트 찾기
    public UserGominResponseDto findGomins(User user) {
        List<Gomin> gomins = gominRepository.findAllByUserId(user.getId());
        return new UserGominResponseDto((long) gomins.size(), gomins);
    }

    //고민글 유효성 검사
    public void checkGomin(GominRequestDto gominRequestDto) {
        if (gominRequestDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("제목입력은 필수값입니다.");
        } else if (gominRequestDto.getContents().isEmpty()) {
            throw new IllegalArgumentException("내용입력은 필수값입니다.");
        } else if (gominRequestDto.getQuestion().isEmpty()) {
            throw new IllegalArgumentException("질문자세대 입력은 필수값입니다.");
        } else if (gominRequestDto.getAnswer().isEmpty()) {
            throw new IllegalArgumentException("답변자세대 입력은 필수값입니다.");
        }
    }

}
