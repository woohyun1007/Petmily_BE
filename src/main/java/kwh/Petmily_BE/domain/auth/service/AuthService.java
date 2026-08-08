package kwh.Petmily_BE.domain.auth.service;

import kwh.Petmily_BE.domain.auth.dto.AuthResponseDto;
import kwh.Petmily_BE.domain.auth.dto.LoginResponseDto;
import kwh.Petmily_BE.domain.auth.dto.TokenDto;
import kwh.Petmily_BE.domain.auth.entity.RefreshToken;
import kwh.Petmily_BE.domain.auth.repository.RefreshTokenRepository;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.user.repository.UserRepository;
import kwh.Petmily_BE.global.error.ErrorCode;
import kwh.Petmily_BE.global.error.exception.BusinessException;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import kwh.Petmily_BE.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
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
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginId, password);

        Authentication authentication;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        String username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        TokenDto tokenDto = tokenService.issueTokensForUser(userId, username);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return AuthResponseDto.from(user, tokenDto);
    }

    @Transactional
    public AuthResponseDto reissue(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        String username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        TokenDto tokenDto = tokenService.issueTokensForUser(userId, username);

        User user = userRepository.findById(savedToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return AuthResponseDto.from(user, tokenDto);
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
