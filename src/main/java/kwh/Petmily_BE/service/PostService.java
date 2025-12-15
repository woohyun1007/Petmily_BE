package kwh.Petmily_BE.service;

import kwh.Petmily_BE.config.SecurityUtil;
import kwh.Petmily_BE.dto.post.PostRequestDto;
import kwh.Petmily_BE.dto.post.PostResponseDto;
import kwh.Petmily_BE.dto.post.PostUpdateRequestDto;
import kwh.Petmily_BE.entity.Pet;
import kwh.Petmily_BE.entity.Post;
import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.enums.PostCategory;
import kwh.Petmily_BE.repository.PetRepository;
import kwh.Petmily_BE.repository.PostRepository;
import kwh.Petmily_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;

    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto) {
        // JWT에서 작성자 정보 획득
        User writer = userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Pet pet = null;

        boolean isRequestPost = requestDto.category() == PostCategory.CAREREQUEST;

        if(isRequestPost) {
            if(requestDto.petId() == null) {
                throw new IllegalArgumentException(requestDto.category() + "카테고리는 반려동물 등록이 필수입니다.");
            }
            pet = petRepository.findById(requestDto.petId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 반려동물을 찾을 수 없습니다."));
            if(!pet.getOwner().getId().equals(writer.getId())) {
                throw new AccessDeniedException("등록하려는 반려동물의 소유자가 아닙니다.");
            }
        } else if(requestDto.petId() != null) {
            pet = petRepository.findById(requestDto.petId()).orElse(null);
        }

        Post post = Post.builder()
                .title(requestDto.title())
                .content(requestDto.content())
                .category(requestDto.category())
                .status(requestDto.status())
                .writer(writer)
                .pet(pet)
                .viewCount(0)
                .build();

        postRepository.save(post);
        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id)
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
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto, PostUpdateRequestDto updateDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        checkPostOwnerShip(post);

        User writer = post.getWriter();
        Pet pet = processPetForPost(requestDto, writer);

        post.update(updateDto, pet);

        return new PostResponseDto(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        checkPostOwnerShip(post);

        postRepository.delete(post);
    }

    // 권한 확인
    private void checkPostOwnerShip(Post post) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        if(!post.getWriter().getId().equals(currentUserId)) {
            throw new AccessDeniedException("해당 게시글을 수정/삭제할 권한이 없습니다.");
        }
    }

    // RequestDto와 writer를 기반으로 Pet을 조회하거나 권한 검증을 한다.
    private Pet processPetForPost(PostRequestDto requestDto, User writer) {
        boolean isPetRequired = isPetRequiredForCategory(requestDto.category());

        if(isPetRequired && requestDto.petId() == null) {
            throw new IllegalArgumentException(requestDto.category() + "카테고리는 반려동물 정보가 필수입니다.");
        }

        if(requestDto.petId() != null) {
            Pet pet = petRepository.findById(requestDto.petId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 반려동물을 찾을 수 없습니다."));

            if(!pet.getOwner().getId().equals(writer.getId())) {
                throw new AccessDeniedException("등록하려는 반려동물의 소유자가 아닙니다.");
            }
            return pet;
        }
        return null;
    }

    // Pet 등록이 필요한 category인지 확인한다.
    private boolean isPetRequiredForCategory(PostCategory category) {
        if (category == PostCategory.CAREREQUEST) {
            return true;
        }
        return false;
    }

}
