package kwh.Petmily_BE.domain.post.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Pet pet = processPetForPost(requestDto.category(), requestDto.petId(), writer);

        Post newPost = requestDto.toEntity(writer,pet);
        return new PostResponseDto(postRepository.save(newPost));
    }

    @Transactional
    public PostResponseDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        post.incrementViewCount();
        return new PostResponseDto(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPosts(int page, int size) {
        // 최신순 정렬
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Repository에서 Page 객체로 조회
        Page<Post> postPage = postRepository.findAll(pageRequest);

        // Page<Post>에서 Page<PostResponseDto>로 변환
        return postPage.map(PostResponseDto::new);
    }

    @Transactional
    public PostResponseDto updatePost(Long userId, Long postId, PostUpdateRequestDto requestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        checkPostOwnerShip(post, userId);

        // Pet 변경이 있다면 검증 후 가져오기
        Pet pet = null;
        if(requestDto.petId() != null) {
            pet = petRepository.findById(requestDto.petId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));

            processPetForPost(requestDto.category(), requestDto.petId(), post.getWriter());
        }

        post.update(requestDto.title(), requestDto.content(), requestDto.region(), requestDto.price(), requestDto.status(), pet);

        return new PostResponseDto(post);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        checkPostOwnerShip(post, userId);

        postRepository.delete(post);
    }

    // 권한 확인
    private void checkPostOwnerShip(Post post, Long userId) {

        if(!post.getWriter().getId().equals(userId)) {
            throw new AccessDeniedException("해당 게시글을 수정/삭제할 권한이 없습니다.");
        }
    }

    // RequestDto와 writer를 기반으로 Pet을 조회하거나 권한 검증을 한다.
    private Pet processPetForPost(PostCategory category, Long petId, User writer) {
        if(category == PostCategory.CAREREQUEST && petId == null) {
            throw new BusinessException(ErrorCode.PET_REQUIRED_FOR_CARE);
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
}
