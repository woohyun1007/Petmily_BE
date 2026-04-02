package kwh.Petmily_BE.domain.post.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kwh.Petmily_BE.domain.post.entity.enums.RequestStatus;
import kwh.Petmily_BE.global.error.ErrorCode;
import kwh.Petmily_BE.global.error.exception.BusinessException;
import kwh.Petmily_BE.domain.post.dto.PostRequestDto;
import kwh.Petmily_BE.domain.post.dto.PostResponseDto;
import kwh.Petmily_BE.domain.post.dto.PostUpdateRequestDto;
import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.post.entity.Post;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.post.entity.enums.PostCategory;
import kwh.Petmily_BE.domain.pet.repository.PetRepository;
import kwh.Petmily_BE.domain.post.repository.PostRepository;
import kwh.Petmily_BE.domain.user.repository.UserRepository;
import kwh.Petmily_BE.global.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;

    @Transactional
    public PostResponseDto createPost(Long userId, PostRequestDto requestDto) {

        // JWT에서 작성자 정보 획득
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Pet pet = null;

        if(requestDto.category() == PostCategory.CARE_REQUEST) {
            pet = petRepository.findByIdAndOwnerId(requestDto.petId(), userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));
            processPetForPost(requestDto,null, pet.getId(), writer);
        }

        Post newPost = requestDto.toEntity(writer, pet);
        return PostResponseDto.from(postRepository.save(newPost));
    }

    @Transactional
    public PostResponseDto getPostById(Long postId, String identifier, HttpServletRequest request, HttpServletResponse response) {
        handleViewCount(postId, identifier, request, response);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        return PostResponseDto.from(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostsWithFilters(PostCategory category, RequestStatus status, String keyword, Pageable pageable) {
        Page<Post> posts;
        if (keyword != null && !keyword.isEmpty()) {
            return postRepository.searchPosts(category, keyword, pageable).map(PostResponseDto::from);
        }
        if (category != null && status == RequestStatus.ALL) {
            posts = postRepository.findAllByCategory(category, pageable);
        } else if (category != null && status != null) {
            posts = postRepository.findByCategoryAndStatus(category, status, pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }

        return posts.map(PostResponseDto::from);
    }

    @Transactional
    public PostResponseDto updatePost(Long userId, Long postId, PostUpdateRequestDto requestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        checkPostOwnerShip(userId, post);

        // Pet 변경이 있다면 검증 후 가져오기
        Pet pet = null;
        if(requestDto.category() == PostCategory.CARE_REQUEST) {
            pet = petRepository.findByIdAndOwnerId(requestDto.petId(), userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));

            processPetForPost(null, requestDto, pet.getId(), post.getWriter());
        }

        // update with latitude and longitude from DTO
        post.update(
                requestDto.title(),
                requestDto.content(),
                requestDto.region(),
                requestDto.priceUnit(),
                requestDto.price(),
                requestDto.status(),
                requestDto.latitude(),
                requestDto.longitude(),
                pet
        );

        return PostResponseDto.from(post);
    }

    @Transactional
    public void deletePost(Long currentUserId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        checkPostOwnerShip(currentUserId, post);

        postRepository.delete(post);
    }

    // 권한 확인
    private void checkPostOwnerShip(Long userId, Post post) {

        if(!post.getWriter().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
    }

    // RequestDto와 writer를 기반으로 Pet을 조회하거나 권한 검증을 한다.
    private Pet processPetForPost(PostRequestDto requestDto, PostUpdateRequestDto updateDto, Long petId, User writer) {
        if(updateDto == null) {
            if (requestDto.region() == null || requestDto.region().isBlank()) throw new BusinessException(ErrorCode.REGION_REQUIRED);
            if (requestDto.price() == null|| requestDto.price() < 0) throw new BusinessException(ErrorCode.INVALID_PRICE);
            if (requestDto.petId() == null) throw new BusinessException(ErrorCode.PET_REQUIRED_FOR_CARE);
        } else if(requestDto == null) {
            if (updateDto.region() == null || updateDto.region().isBlank())
                throw new BusinessException(ErrorCode.REGION_REQUIRED);
            if (updateDto.price() == null || updateDto.price() < 0)
                throw new BusinessException(ErrorCode.INVALID_PRICE);
            if (updateDto.petId() == null) throw new BusinessException(ErrorCode.PET_REQUIRED_FOR_CARE);
        }
        if(petId != null) {
            Pet pet = petRepository.findById(petId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));

            if(!pet.getOwner().getId().equals(writer.getId())) {
                throw new AccessDeniedException("본인의 반려동물만 등록 가능합니다.");
            }
            return pet;
        }
        return null;
    }

    private void handleViewCount(Long postId, String identifier, HttpServletRequest request, HttpServletResponse response) {
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {return;}
        Cookie[] cookies = request.getCookies();
        String cookieName = identifier + "viewed_post_" + postId;
        boolean isVisited = false;

        // 이미 해당 게시글을 조회했는지 쿠키 확인
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals(cookieName)) {
                    isVisited = true;
                    break;
                }
            }
        }

        // 방문하지 않았을 때만 조회수 증가 및 쿠키 발급
        if(!isVisited) {
            postRepository.updateViewCount(postId);

            ResponseCookie newCookie = ResponseCookie.from(cookieName, "true")
                    .path("/")        // 모든 경로에서 쿠키 유효
                    .httpOnly(true)    // 자바스크립트 접근 방지(보안)
                    .maxAge(60*60)
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, newCookie.toString());
        }
    }
}
