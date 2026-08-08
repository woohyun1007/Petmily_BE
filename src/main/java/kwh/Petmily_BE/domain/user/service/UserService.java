package kwh.Petmily_BE.domain.user.service;

import kwh.Petmily_BE.domain.auth.service.EmailVerificationService;
import jakarta.persistence.EntityManager;
import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.pet.repository.PetRepository;
import kwh.Petmily_BE.domain.post.repository.CommentRepository;
import kwh.Petmily_BE.domain.post.repository.PostRepository;
import kwh.Petmily_BE.domain.user.dto.PasswordUpdateRequestDto;
import kwh.Petmily_BE.domain.user.dto.UserRequestDto;
import kwh.Petmily_BE.domain.user.dto.UserResponseDto;
import kwh.Petmily_BE.domain.user.dto.UserUpdateRequestDto;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.user.repository.UserRepository;
import kwh.Petmily_BE.global.error.ErrorCode;
import kwh.Petmily_BE.global.error.exception.BusinessException;
import kwh.Petmily_BE.global.file.FileService;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PetRepository petRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FileService fileService;
    private final EmailVerificationService emailVerificationService;
    private final EntityManager em;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return new CustomUserDetails(user);
    }

    @Transactional
    public UserResponseDto signUp(UserRequestDto requestDto) {
        if (userRepository.existsByLoginId(requestDto.loginId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (userRepository.existsByNickname(requestDto.nickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (!requestDto.password().equals(requestDto.passwordConfirm())) {
            throw new BusinessException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }
        emailVerificationService.requireVerifiedEmail(requestDto.email());

        String encodedPassword = passwordEncoder.encode(requestDto.password());
        User user = requestDto.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                emailVerificationService.consumeVerifiedEmail(requestDto.email());
            }
        });
        return UserResponseDto.from(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(Long userId) {
        return userRepository.findById(userId)
                .map(UserResponseDto::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public UserResponseDto updateMyInfo(Long userId, UserUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (userRepository.existsByNicknameAndIdNot(requestDto.nickname(), userId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        user.updateProfile(requestDto.nickname());
        return UserResponseDto.from(user);
    }

    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.currentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        if (passwordEncoder.matches(requestDto.newPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        String encodedPassword = passwordEncoder.encode(requestDto.newPassword());
        user.updatePassword(encodedPassword);
    }

    @Transactional
    public void deleteMyInfo(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        List<Long> writerPostIds = postRepository.findIdsByWriterId(userId);
        if (!writerPostIds.isEmpty()) {
            commentRepository.deleteAllByPostIds(writerPostIds);
            postRepository.deleteAllByIdInBatch(writerPostIds);
        }

        List<Pet> pets = petRepository.findByOwner_Id(userId);
        for (Pet pet : pets) {
            List<Long> petPostIds = postRepository.findIdsByPetId(pet.getId());
            if (!petPostIds.isEmpty()) {
                commentRepository.deleteAllByPostIds(petPostIds);
                postRepository.deleteAllByPetId(pet.getId());
            }
        }

        for (Pet pet : pets) {
            if (pet.getImageUrl() != null && !pet.getImageUrl().isBlank()) {
                fileService.deleteFile(pet.getImageUrl());
            }
        }

        if (!pets.isEmpty()) {
            petRepository.deleteAll(pets);
            em.flush();
        }

        commentRepository.deleteAllByWriterId(userId);
        userRepository.deleteById(userId);
        em.flush();
        em.clear();
    }
}
