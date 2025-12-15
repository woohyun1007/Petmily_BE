package kwh.Petmily_BE.service;

import kwh.Petmily_BE.config.SecurityUtil;
import kwh.Petmily_BE.dto.users.CustomUserDetails;
import kwh.Petmily_BE.dto.users.JoinRequestDto;
import kwh.Petmily_BE.dto.users.JoinResponseDto;
import kwh.Petmily_BE.dto.users.UserUpdateRequestDto;
import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + loginId));

        // Optional 처리가 위에서 끝났으므로, 바로 User 객체를 전달합니다.
        return new CustomUserDetails(user);
    }

    // 회원가입
    @Transactional
    public JoinResponseDto signUp(JoinRequestDto requestDto) {
        if(userRepository.findByLoginId(requestDto.loginId()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 사용자입니다.");
        }
        //비밀번호 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(requestDto.password());

        //RequestDto -> User Entity 변환
        User newUser = User.builder()
                .loginId(requestDto.loginId())
                .email(requestDto.email())
                .password(encodedPassword)
                .username(requestDto.username())
                .roles(requestDto.roles())
                .build();

        //DB에 저장
        User savedUser = userRepository.save(newUser);

        //Entity -> ReponseDto 변환 후 반환
        return new JoinResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public JoinResponseDto getMyInfo() {
        System.out.println("OK");
        // 현재 인증된 사용자 ID 획득
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // ID를 기반으로 DB에서 User 엔티티 조회
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

        // User Entity를 JoinResponseDto로 변환하여 반환
        return new JoinResponseDto(user);
    }

    // 회원정보 업데이트
    @Transactional
    public JoinResponseDto updateMyInfo(UserUpdateRequestDto requestDto) {
        // 현재 인증된 사용자 ID 획득
        Long currentUserId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

        // User 엔티티의 수정 로직 호출
        user.updateFromDto(requestDto);

        // 비밀번호 암호화 처리
        if(requestDto.password() != null && !requestDto.password().isEmpty()) {
            String hashedPassword = bCryptPasswordEncoder.encode(requestDto.password());
            user.updatePassword(hashedPassword);
        }
        return new JoinResponseDto(user);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteMyInfo() {
        // 소유권 확인
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 정보 삭제
        userRepository.deleteById(currentUserId);
    }
}
