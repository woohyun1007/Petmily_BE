package kwh.Petmily_BE.controller;

import kwh.Petmily_BE.dto.post.PostRequestDto;
import kwh.Petmily_BE.dto.post.PostResponseDto;
import kwh.Petmily_BE.dto.post.PostUpdateRequestDto;
import kwh.Petmily_BE.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시글 생성
    @PostMapping("/create")
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto requestDto) {
        PostResponseDto responseDto = postService.createPost(requestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 게시글 단건 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable("postId") Long postId) {
        PostResponseDto responseDto = postService.getPostById(postId);

        return ResponseEntity.ok(responseDto);
    }

    // 게시글 목록 조회 (exURL : /post?page=0&size=10)
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<PostResponseDto> responsePage = postService.getAllPosts(page, size);

        return ResponseEntity.ok(responsePage);
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable("postId") Long postId, @RequestBody PostUpdateRequestDto updateDto, @RequestBody PostRequestDto requestDto) {
        PostResponseDto responseDto = postService.updatePost(postId, requestDto, updateDto);

        return ResponseEntity.ok(responseDto);
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") Long postId) {
        postService.deletePost(postId);

        return ResponseEntity.noContent().build();
    }
}
