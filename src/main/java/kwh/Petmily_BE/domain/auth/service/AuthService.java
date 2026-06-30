package kwh.Petmily_BE.domain.auth.service;

import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.auth.dto.AuthResponseDto;
import kwh.Petmily_BE.domain.auth.dto.LoginResponseDto;
import kwh.Petmily_BE.domain.auth.dto.TokenDto;
import kwh.Petmily_BE.domain.auth.entity.RefreshToken;
import kwh.Petmily_BE.domain.auth.repository.RefreshTokenRepository;
import kwh.Petmily_BE.domain.user.repository.UserRepository;
import kwh.Petmily_BE.global.error.ErrorCode;
import kwh.Petmily_BE.global.error.exception.BusinessException;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import kwh.Petmily_BE.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Transactional
    public AuthResponseDto login(String loginId, String password) {
        // ID/PW를 기반으로 Authentication Token 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginId, password);

        // 검증
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증 정보를 기반으로 JWT 토큰 생성
        // TokenService에 위임
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        String username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        TokenDto tokenDto = tokenService.issueTokensForUser(userId, username);

        // User 추가 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return AuthResponseDto.from(user, tokenDto);
    }

    @Transactional
    public AuthResponseDto reissue(String refreshToken) {
        // 토큰 유효성 검사
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // DB에 해당 토큰이 있는지 확인
        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        // 토큰에서 유저 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        // 새로운 토큰 생성
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        String username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        TokenDto tokenDto = tokenService.issueTokensForUser(userId, username);

        // 유저 정보 조회 및 최종 응답
        User user = userRepository.findById(savedToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return AuthResponseDto.from(user,tokenDto);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public LoginResponseDto getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return LoginResponseDto.of(user);
    }
}
