package kwh.Petmily_BE.domain.user.service;

import com.sun.jdi.request.DuplicateRequestException;
import jakarta.persistence.EntityNotFoundException;
import kwh.Petmily_BE.domain.user.dto.*;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.user.repository.UserRepository;
import kwh.Petmily_BE.global.error.exception.BusinessException;
import kwh.Petmily_BE.global.error.exception.UserNotFoundException;
import kwh.Petmily_BE.global.error.ErrorCode;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        //Entity -> ReponseDto 변환 후 반환
        return new UserResponseDto(userRepository.save(requestDto.toEntity(passwordEncoder.encode(requestDto.password()))));
    }

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(Long userId) {

        return userRepository.findById(userId)
                .map(UserResponseDto::new)
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

        return new UserResponseDto(user);
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
        userRepository.deleteById(userId);
    }
}
