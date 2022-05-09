package com.example.seesaw.controller;


import com.example.seesaw.dto.PostCommentRequestDto;
import com.example.seesaw.security.UserDetailsImpl;
import com.example.seesaw.service.PostCommentLikeService;
import com.example.seesaw.service.PostCommentService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/post/comment")
public class PostCommentController {

    private final PostCommentService postCommentService;
    private final PostCommentLikeService postCommentLikeService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> registerPostComment(
            @PathVariable(name = "postId") Long postId,
            @RequestBody PostCommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        postCommentService.registerPostComment(postId, requestDto, userDetails.getUser());
        return ResponseEntity.ok()
                .body("단어장 예시등록 완료");
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<String> updatePostComment(
            @PathVariable Long commentId,
            @RequestBody PostCommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        postCommentService.updatePostComment(commentId, requestDto, userDetails.getUser());
        return ResponseEntity.ok()
                .body("단어장 예시수정 완료");
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deletePostComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        postCommentService.deletePostComment(commentId, userDetails.getUser());
        return ResponseEntity.ok()
                .body("단어장 예시삭제 완료");
    }

    @ApiOperation("예시 좋아요")
    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> likeComment(@PathVariable Long commentId) {

        postCommentLikeService.likeComment(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("예시 좋아요 취소")
    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<String> unlikeComment(@PathVariable Long commentId) {

        postCommentLikeService.unlikeComment(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}



