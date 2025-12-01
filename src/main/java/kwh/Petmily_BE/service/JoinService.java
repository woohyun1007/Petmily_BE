package kwh.Petmily_BE.service;


import kwh.Petmily_BE.dto.JoinRequestDto;
import kwh.Petmily_BE.dto.JoinResponseDto;
import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
                .roles(Set.of(requestDto.roles()))
                .build();

        //DB에 저장
        User savedUser = userRepository.save(newUser);

        //Entity -> ReponseDto 변환 후 반환
        return new JoinResponseDto(savedUser);
    }
}
