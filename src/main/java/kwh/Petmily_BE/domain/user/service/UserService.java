package kwh.Petmily_BE.domain.user.service;

import com.sun.jdi.request.DuplicateRequestException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import kwh.Petmily_BE.domain.user.dto.*;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.user.repository.UserRepository;
import kwh.Petmily_BE.global.error.exception.BusinessException;
import kwh.Petmily_BE.global.error.exception.UserNotFoundException;
import kwh.Petmily_BE.global.error.ErrorCode;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.pet.repository.PetRepository;
import kwh.Petmily_BE.domain.post.entity.Post;
import kwh.Petmily_BE.domain.post.repository.PostRepository;
import kwh.Petmily_BE.domain.post.repository.CommentRepository;
import kwh.Petmily_BE.domain.pet.service.PetService;
import kwh.Petmily_BE.global.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PetRepository petRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PetService petService;
    private final FileService fileService;
    private final EntityManager em;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + loginId));

        // Optional 처리가 위에서 끝났으므로, 바로 User 객체를 전달합니다.
        return new CustomUserDetails(user);
    }

    // 회원가입
    @Transactional
    public UserResponseDto signUp(UserRequestDto requestDto) {
        if(userRepository.existsByLoginId(requestDto.loginId())) {
            throw new DuplicateRequestException("이미 가입된 사용자입니다.");
        }
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        User user = requestDto.toEntity(encodedPassword);

        //Entity -> ReponseDto 변환 후 반환
        User savedUser = userRepository.save(user);
        return UserResponseDto.from(savedUser);
    }

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(Long userId) {

        return userRepository.findById(userId)
                .map(UserResponseDto::from)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));

    }

    // 회원정보 업데이트
    @Transactional
    public UserResponseDto updateMyInfo(Long userId, UserUpdateRequestDto requestDto) {
        // 현재 인증된 사용자 ID 획득
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));

        // User 엔티티의 수정 로직 호출
        user.updateProfile(requestDto.email(), requestDto.nickname());

        return UserResponseDto.from(user);
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 1. 현재 비밀번호 일치 여부 확인 (BCrypt 매칭)
        if (!passwordEncoder.matches(requestDto.currentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD); // "현재 비밀번호가 틀립니다" 에러
        }

        // 2. 새 비밀번호와 이전 비밀번호가 같은지 체크 (선택 사항이지만 권장)
        if (passwordEncoder.matches(requestDto.newPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        // 3. 비밀번호 암호화 후 업데이트
        String encodedPassword = passwordEncoder.encode(requestDto.newPassword());
        user.updatePassword(encodedPassword);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteMyInfo(Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        // 1) 사용자가 작성한 게시글들 먼저 정리 (댓글 -> 게시글)
        List<Long> writerPostIds = postRepository.findIdsByWriterId(userId);
        if (!writerPostIds.isEmpty()) {
            commentRepository.deleteAllByPostIds(writerPostIds);
            postRepository.deleteAllByIdInBatch(writerPostIds);
        }

        // 2) 사용자가 소유한 펫들 관련 게시글/댓글 정리
        List<Pet> pets = petRepository.findByOwner_Id(userId);
        for (Pet pet : pets) {
            List<Long> petPostIds = postRepository.findIdsByPetId(pet.getId());
            if (!petPostIds.isEmpty()) {
                commentRepository.deleteAllByPostIds(petPostIds);
                postRepository.deleteAllByPetId(pet.getId());
            }
        }

        // 2.5) 삭제될 펫들의 이미지 파일 제거
        for (Pet pet : pets) {
            if (pet.getImageUrl() != null && !pet.getImageUrl().isBlank()) {
                fileService.deleteFile(pet.getImageUrl());
            }
        }

        // 3) 펫 일괄 삭제 - JPA deleteAll으로 확실히 제거
        if (!pets.isEmpty()) {
            petRepository.deleteAll(pets);
            // 즉시 DB에 반영하여 FK 제약으로 인한 문제를 방지
            em.flush();
        }

        // 4) 사용자가 작성한 남은 댓글 삭제
        commentRepository.deleteAllByWriterId(userId);

        // 5) 사용자 삭제
        userRepository.deleteById(userId);

        // 영속성 컨텍스트 동기화 및 정리
        em.flush();
        em.clear();
    }
}
