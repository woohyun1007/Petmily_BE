package kwh.Petmily_BE.domain.auth.service;

import kwh.Petmily_BE.domain.auth.dto.TokenDto;
import kwh.Petmily_BE.domain.auth.entity.RefreshToken;
import kwh.Petmily_BE.domain.auth.repository.RefreshTokenRepository;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import kwh.Petmily_BE.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 주어진 사용자 정보로 Authentication 객체를 만들어 access/refresh 토큰을 생성하고
     * refresh 토큰을 DB에 저장(또는 업데이트)한 뒤 TokenDto를 반환합니다.
     */
    public TokenDto issueTokensForUser(Long userId, String username) {
        CustomUserDetails userDetails = new CustomUserDetails(userId, username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

        String accessToken = jwtTokenProvider.createToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        TokenDto tokenDto = TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        RefreshToken tokenEntity = refreshTokenRepository.findByUserId(userId)
                .orElse(new RefreshToken(userId, refreshToken));
        tokenEntity.updateToken(refreshToken);
        refreshTokenRepository.save(tokenEntity);

        return tokenDto;
    }
}
