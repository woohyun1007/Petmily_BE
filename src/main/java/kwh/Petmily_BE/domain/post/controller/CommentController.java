package kwh.Petmily_BE.domain.post.controller;

import jakarta.validation.Valid;
import kwh.Petmily_BE.domain.post.dto.CommentRequestDto;
import kwh.Petmily_BE.domain.post.dto.CommentResponseDto;
import kwh.Petmily_BE.domain.post.dto.CommentUpdateRequestDto;
import kwh.Petmily_BE.domain.post.service.CommentService;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    // 댓글 등록
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CommentRequestDto requestDto) {
        CommentResponseDto responseDto = commentService.createComment(postId, userDetails.getId(), requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 댓글 조회
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable("postId") Long postId) {
        List<CommentResponseDto> responseDto = commentService.getComments(postId);
        return ResponseEntity.ok(responseDto);
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CommentUpdateRequestDto requestDto) {
        CommentResponseDto responseDto = commentService.updateComment(commentId, userDetails.getId(), requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
