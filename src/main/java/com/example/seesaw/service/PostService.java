package com.example.seesaw.service;

import com.example.seesaw.dto.*;
import com.example.seesaw.model.*;
import com.example.seesaw.repository.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Builder
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostTagRepository postTagRepository;
    private final PostS3Service postS3Service;
    private final UserService userService;
    private final ConvertTimeService convertTimeService;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;

    //단어장 작성
    @Transactional
    public void createPost(PostRequestDto requestDto, List<MultipartFile> files, User user) {

        // 단어장 작성시 이미지파일 없이 등록시 기본 이미지 파일로 올리기!
        String name = null;
        for(MultipartFile file:files){
            name = file.getOriginalFilename();
            System.out.println("file이름은~~~:" + name);
        }
        List<String> imagePaths = new ArrayList<>();
        if (name.equals("")) {
            imagePaths.add("기본이미지 AWS에 저장해서 주소넣기!");
        } else {
            imagePaths.addAll(postS3Service.upload(files));

        }
        // 단어
        String title = requestDto.getTitle();
        // 내용
        String contents = requestDto.getContents();
        // videoUrl
        String videoUrl = requestDto.getVideoUrl();
        // generation
        String generation = requestDto.getGeneration();
        // 태그
        List<String> tagName = requestDto.getTagNames();

        if (title == null) {
            throw new IllegalArgumentException("등록할 단어를 적어주세요.");
        } else if (contents == null) {
            throw new IllegalArgumentException("등록할 단어의 내용을 적어주세요.");
        }

        //post 저장
        Post post = new Post(title, contents, videoUrl, generation, user);
        postRepository.save(post);

        //이미지 URL 저장하기
        List<String> images = new ArrayList<>();
        for(String imageUrl : imagePaths){
            PostImage postImage = new PostImage(imageUrl, post);
            postImageRepository.save(postImage);
            images.add(postImage.getPostImages());
        }

        // tag 저장하기.
        List<String> tags = new ArrayList<>();
        for(String tagNames : tagName){
            PostTag postTag = new PostTag(tagNames, post);
            postTagRepository.save(postTag);
            tags.add(postTag.getTagName());
        }

        //return 값 생성
        new PostResponseDto(post, images, tags);
    }

    // 중복체크
    public boolean wordCheck(String title) {
        return postRepository.existsByTitle(title);
    }

    // 단어장 수정
    public void updatePost(Long postId, PostRequestDto requestDto, List<MultipartFile> files, User user) {

        // 해당 단어장 체크.
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 단어장이 없습니다.")
        );


        //post update
        post.update(requestDto, user);

        // 이미지 name 확인
        String name = null;
        for(MultipartFile file:files){
            name = file.getOriginalFilename();
            System.out.println("file이름은~~~:" + name);
        }

        // 이미지 수정작업
        List<String> imagePaths = new ArrayList<>();
        if (name.equals("") && requestDto.getPostImages().get(0).isEmpty()) {
            imagePaths.add("기본이미지 AWS에 저장해서 주소넣기!");
            postS3Service.delete(postId, requestDto.getPostImages());
            postImageRepository.deleteAllByPostId(postId);
        } else if(!name.equals("")) {
            imagePaths.addAll(postS3Service.update(postId, requestDto.getPostImages(), files));
        } else{
            imagePaths = requestDto.getPostImages();
            postS3Service.delete(postId, requestDto.getPostImages());
            postImageRepository.deleteAllByPostId(postId);
        }

        //이미지 URL 저장
        for(String imageUrl : imagePaths){
            PostImage postImage = new PostImage(imageUrl, post);
            postImageRepository.save(postImage);

        }

        // tag 저장
        List<String> tagName = requestDto.getTagNames();
        postTagRepository.deleteAllByPostId(postId);
        for(String tagNames : tagName){
            PostTag postTag = new PostTag(tagNames, post);
            postTagRepository.save(postTag);
        }

    }

    // 단어 상세 보기.
    public PostDetailResponseDto findDetailPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("고민 Id에 해당하는 글이 없습니다.")
        );
        PostResponseDto postResponseDto = postTagAndImages(postId);

        PostDetailResponseDto postDetailResponseDto = new PostDetailResponseDto(postResponseDto);
        postDetailResponseDto.setNickname(post.getUser().getNickname());
        postDetailResponseDto.setTitle(post.getTitle());
        postDetailResponseDto.setContents(post.getContents());
        postDetailResponseDto.setGeneration(post.getGeneration());
        postDetailResponseDto.setVideoUrl(post.getVideoUrl());
        postDetailResponseDto.setProfileImages(userService.findUserProfiles(post.getUser()));
        String postTime = convertTimeService.convertLocaldatetimeToTime(post.getCreatedAt());
        postDetailResponseDto.setPostTime(postTime);
        postDetailResponseDto.setViews(post.getViews());
        post.setViews(post.getViews()+1);
        postRepository.save(post);

        List<PostComment> postComments = postCommentRepository.findAllByPostIdOrderByLikeCountDesc(postId);
        postDetailResponseDto.setCommentCount((long) postComments.size());
        List<PostCommentRequestDto> postCommentRequestDtos = new ArrayList<>();
        for(PostComment postComment:postComments){
            PostCommentRequestDto postCommentRequestDto = new PostCommentRequestDto(postComment);
            User user = userRepository.findByNickname(postComment.getNickname()).orElseThrow(
                    () -> new IllegalArgumentException("고민댓글에 해당하는 사용자를 찾을 수 없습니다."));
            postCommentRequestDto.setProfileImages(userService.findUserProfiles(user));
            postCommentRequestDtos.add(postCommentRequestDto);
        }
        postDetailResponseDto.setPostComments(postCommentRequestDtos);

        return postDetailResponseDto;
    }

    public PostResponseDto postTagAndImages(Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 단어가 없습니다.")
        );

        List<PostTag> postTags = postTagRepository.findAllByPostId(postId);
        if (postTags.isEmpty()){
            throw new IllegalArgumentException("해당하는 태그가 없습니다.");
        }
        List<String> postTagList = new ArrayList<>();
        for(PostTag postTag : postTags){
            postTagList.add(postTag.getTagName());
        }

        List<PostImage> postImages = postImageRepository.findAllByPostId(postId);
        if (postImages.isEmpty()){
            throw new IllegalArgumentException("해당하는 이미지가 없습니다.");
        }
        List<String> postImageList = new ArrayList<>();
        for(PostImage postImage : postImages){
            postImageList.add(postImage.getPostImages());
        }

        return new PostResponseDto(post, postImageList, postTagList);
    }

    @Transactional
    public List<PostSearchResponseDto> searchPosts(String title, String contents) {
        List<Post> posts = postRepository.findByTitleContainingOrContentsContaining(title,contents);
        List<PostSearchResponseDto> searchList = new ArrayList<>();

        if (posts.isEmpty())
            return searchList;

        for (Post post : posts) {
            searchList.add(this.convertEntityToDto(post));
        }
        return searchList;
    }
    private PostSearchResponseDto convertEntityToDto(Post post) {

        return PostSearchResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .generation(post.getGeneration())
                .build();
    }
}
