package com.example.seesaw.service;

import com.example.seesaw.dto.*;
import com.example.seesaw.model.*;
import com.example.seesaw.repository.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public void createPost(PostRequestDto requestDto, List<MultipartFile> files, User user) {

        // 단어장 작성시 이미지파일 없이 등록시 기본 이미지 파일로 올리기!
        List<String> imagePaths = new ArrayList<>();
        if (files == null) {
            imagePaths.add("https://myseesaw.s3.ap-northeast-2.amazonaws.com/ddddd23sdfasf.jpg");
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
        List<String> tagNames = requestDto.getTagNames();

        if (title == null) {
            throw new IllegalArgumentException("등록할 단어를 적어주세요.");
        } else if (contents == null) {
            throw new IllegalArgumentException("등록할 단어의 내용을 적어주세요.");
        }

        //post 저장
        Post post = new Post(title, contents, videoUrl, generation, user, 0);
        postRepository.save(post);

        //이미지 URL 저장하기
        for(String imageUrl : imagePaths){
            PostImage postImage = new PostImage(imageUrl, post);
            postImageRepository.save(postImage);
        }
        // tag 저장하기.
        for(String tagName : tagNames){
            if(!tagName.equals("")){
                PostTag postTag = new PostTag(tagName, post);
                postTagRepository.save(postTag);
            }
        }
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


        // 이미지 수정작업
        List<String> imagePaths = new ArrayList<>();
        if (files==null && requestDto.getPostImages().isEmpty()) {
            imagePaths.add("기본이미지 AWS에 저장해서 주소넣기!");
            postS3Service.delete(postId, requestDto.getPostImages());
            postImageRepository.deleteAllByPostId(postId);
        } else if(files!=null) {
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
    public PostDetailResponseDto findDetailPost(Long postId, int page) {
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
        postDetailResponseDto.setPostUpdateTime(postTime);
        postDetailResponseDto.setViews(post.getViews());
        post.setViews(post.getViews()+1);
        postRepository.save(post);

        // paging 처리
        Pageable pageable = PageRequest.of(page-1, 4);
        Page<PostComment> postCommentPage = postCommentRepository.findAllByPostIdOrderByLikeCountDesc(postId,pageable);

        // 댓글 개수
        List<PostComment> postComments = postCommentRepository.findAllByPostId(postId);
        postDetailResponseDto.setCommentCount((long) postComments.size());


        List<PostCommentRequestDto> postCommentRequestDtos = new ArrayList<>();
        for(PostComment postComment:postCommentPage){
            PostCommentRequestDto postCommentRequestDto = new PostCommentRequestDto(postComment);
            User user = userRepository.findByNickname(postComment.getNickname()).orElseThrow(
                    () -> new IllegalArgumentException("고민댓글에 해당하는 사용자를 찾을 수 없습니다."));
            postCommentRequestDto.setProfileImages(userService.findUserProfiles(user));
            postCommentRequestDto.setCommentLikeCount(postComment.getLikeCount());
            String postCommentTime = convertTimeService.convertLocaldatetimeToTime(postComment.getCreatedAt());
            postCommentRequestDto.setCommentTime(postCommentTime);
            postCommentRequestDtos.add(postCommentRequestDto);

        }
        postDetailResponseDto.setPostComments(postCommentRequestDtos);

        return postDetailResponseDto;
    }

    // 현재 사용자 정보(토큰에 있는 정보를 활용?!) //https://00hongjun.github.io/spring-security/securitycontextholder/ 참고.
    private User getCurrentUser() {
        String nickname = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(nickname)
                .orElseThrow(() -> new RuntimeException("Error : User is not found"));
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
    public PostSearchDto searchPosts(String title, String contents) {
        List<Post> posts = postRepository.findByTitleContainingOrContentsContaining(title,contents);
        List<PostSearchResponseDto> searchList = new ArrayList<>();
        PostSearchDto postSearchList = new PostSearchDto();
        if (posts.isEmpty())
            return postSearchList;

        for (Post post : posts) {
            searchList.add(this.convertEntityToDto(post));
        }
        Long size = (long) searchList.size();
        postSearchList.setListCount(size);
        postSearchList.setPostSearchList(searchList);

        return postSearchList;
    }
    private PostSearchResponseDto convertEntityToDto(Post post) {
        List<PostComment> postComments = postCommentRepository.findAllByPostId(post.getId());

        return PostSearchResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .generation(post.getGeneration())
                .imageCount((long)post.getPostImages().size())
                .commentCount((long)postComments.size())
                .build();
    }

    // 스크랩 순으로 매인페이지 조회
    public List<PostScrapSortResponseDto> findAllPosts(){
        // 스크랩 수가 많은 순서대로 16개의 post를 담는다.
        List<Post> posts = postRepository.findTop16ByOrderByScrapCountDesc();
        // 리스트 형태 Dto 타입 빈 객체 생성
        List<PostScrapSortResponseDto> postScrapSortResponseDtos = new ArrayList<>();
        // posts 하나씩 꺼내어 처리
        for (Post post: posts) {
            // postId 에 해당하는 post image 를 가져온다.
            List<PostImage> postImages = postImageRepository.findAllByPostId(post.getId());
            String imageUrl = "";
            for (PostImage postImage: postImages
                 ) {
                // 하나만 뽑아서 break
                imageUrl = postImage.getPostImages();
                break;
            }
            postScrapSortResponseDtos.add(new PostScrapSortResponseDto(post, imageUrl));
        }

        return postScrapSortResponseDtos;
    }

}
