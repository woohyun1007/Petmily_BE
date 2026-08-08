package kwh.Petmily_BE.domain.post.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kwh.Petmily_BE.domain.post.dto.PostRequestDto;
import kwh.Petmily_BE.domain.post.dto.PostResponseDto;
import kwh.Petmily_BE.domain.post.dto.PostUpdateRequestDto;
import kwh.Petmily_BE.domain.post.entity.enums.PostCategory;
import kwh.Petmily_BE.domain.post.entity.enums.RequestStatus;
import kwh.Petmily_BE.domain.post.service.PostService;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<PostResponseDto> createPost(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody PostRequestDto requestDto) {
        PostResponseDto responseDto = postService.createPost(userDetails.getId(), requestDto);

        return ResponseEntity.ok(responseDto);
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostDetail(@PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails userDetails, HttpServletRequest request, HttpServletResponse response) {
        String identifier = (userDetails != null) ? String.valueOf(userDetails.getId()) : "guest";
        PostResponseDto responseDto = postService.getPostById(postId, identifier, request, response);

        return ResponseEntity.ok(responseDto);
    }

    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getPosts(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size, @RequestParam(name = "category", required = false) String categorystr, @RequestParam(name = "sort", defaultValue = "modifiedAt,desc") String sort, @RequestParam(name = "status", required = false) String statusStr, @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = buildPageable(page, size, sort);

        PostCategory category = null;
        if(categorystr != null && !categorystr.isEmpty() && !categorystr.equals("undefined")) {
            try {
                category = PostCategory.valueOf(categorystr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        RequestStatus status = null;
        if(statusStr != null && !statusStr.isEmpty()) {
            try {
                status = RequestStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(postService.getPostsWithFilters(category, status, keyword, pageable));
    }

    private Pageable buildPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortProperty = sortParams[0].trim();
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Sort sortOrder = Sort.by(direction, sortProperty);
        if ("viewCount".equalsIgnoreCase(sortProperty)) {
            sortOrder = sortOrder.and(Sort.by(Sort.Direction.DESC, "modifiedAt"));
        }

        return PageRequest.of(page, size, sortOrder);
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
