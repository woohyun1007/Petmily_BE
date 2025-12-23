package kwh.Petmily_BE.domain.post.controller;

import kwh.Petmily_BE.domain.post.dto.PostRequestDto;
import kwh.Petmily_BE.domain.post.dto.PostResponseDto;
import kwh.Petmily_BE.domain.post.dto.PostUpdateRequestDto;
import kwh.Petmily_BE.domain.post.service.PostService;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시글 생성
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody PostRequestDto requestDto) {
        PostResponseDto responseDto = postService.createPost(userDetails.getId(), requestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 게시글 단건 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable("postId") Long postId) {
        PostResponseDto responseDto = postService.getPostById(postId);

        return ResponseEntity.ok(responseDto);
    }

    // 게시글 목록 조회 (ex. URL : /post?page=0&size=10)
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<PostResponseDto> responsePage = postService.getAllPosts(page, size);

        return ResponseEntity.ok(responsePage);
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("postId") Long postId, @RequestBody PostUpdateRequestDto requestDto) {
        PostResponseDto responseDto = postService.updatePost(userDetails.getId(), postId, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("postId") Long postId) {
        postService.deletePost(userDetails.getId(), postId);

        return ResponseEntity.noContent().build();
    }
}
