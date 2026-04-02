package kwh.Petmily_BE.domain.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    @Value("${KAKAO_CLIENT_SECRET:}")
    private String kakaoClientSecret;

    @Value("${KAKAO_REDIRECT_URI}")
    private String kakaoRedirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginResponseDto loginWithCode(String code) {
        try {
            // 1) 토큰 교환
            String tokenUrl = "https://kauth.kakao.com/oauth/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", kakaoClientId);
            params.add("redirect_uri", kakaoRedirectUri);
            params.add("code", code);
            if (kakaoClientSecret != null && !kakaoClientSecret.isBlank()) params.add("client_secret", kakaoClientSecret);

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);
            ResponseEntity<String> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, String.class);

            JsonNode tokenJson = objectMapper.readTree(tokenResponse.getBody());
            String accessToken = tokenJson.get("access_token").asText();

            // 2) 사용자 정보 조회
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);
            userHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);
            ResponseEntity<String> userResponse = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, userRequest, String.class);
            JsonNode userJson = objectMapper.readTree(userResponse.getBody());

            Long kakaoId = userJson.get("id").asLong();

            JsonNode kakaoAccount = userJson.get("kakao_account");
            String email = null;
            if (kakaoAccount != null && kakaoAccount.has("email")) {
                email = kakaoAccount.get("email").asText(null);
            }

            JsonNode properties = userJson.get("properties");
            String nickname = null;
            String profileImage = null;
            if (properties != null) {
                nickname = properties.has("nickname") ? properties.get("nickname").asText(null) : null;
                profileImage = properties.has("profile_image") ? properties.get("profile_image").asText(null) : null;
            }

            // 3) DB에서 기존 사용자 확인 또는 생성
            Optional<User> existing = userRepository.findByKakaoId(kakaoId);
            User user;
            if (existing.isPresent()) {
                user = existing.get();
            } else {
                // 이메일/로그인ID가 충돌할 수 있으므로 간단한 처리: 로그인ID에 kakao_{id} 사용
                String loginId = "kakao_" + kakaoId;
                if (email == null || email.isBlank()) {
                    // 이메일이 없으면 임의로 생성하거나 null 허용 방식으로 처리 (현재 User.email은 not null, unique)
                    // 여기서는 kakao 이메일이 없으면 예외를 던지지 않고 임시 이메일 생성
                    email = loginId + "@kakao.local";
                }

                // 기본 비밀번호는 빈 문자열 또는 랜덤값을 넣고, OAuth 로그인만 허용하도록 처리
                user = User.builder()
                        .loginId(loginId)
                        .password("")
                        .email(email)
                        .nickname(nickname != null ? nickname : loginId)
                        .roles(java.util.Set.of())
                        .kakaoId(kakaoId)
                        .build();

                user = userRepository.save(user);
            }

            // 4) JWT 발급 (프로젝트의 JwtTokenProvider를 이용)
            // 현재 JwtTokenProvider.createToken은 Authentication을 받으므로, 임시 Authentication 생성
            CustomUserDetails userDetails = new CustomUserDetails(user.getId(), user.getLoginId(), java.util.Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")));
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken authenticationToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            String accessJwt = jwtTokenProvider.createToken(authenticationToken);
            String refreshJwt = jwtTokenProvider.createRefreshToken(authenticationToken);

            TokenDto tokenDto = TokenDto.builder()
                    .grantType("Bearer")
                    .accessToken(accessJwt)
                    .refreshToken(refreshJwt)
                    .build();

            // RefreshToken 저장/업데이트
            RefreshToken tokenEntity = refreshTokenRepository.findByUserId(user.getId()).orElse(new RefreshToken(user.getId(), refreshJwt));
            tokenEntity.updateToken(refreshJwt);
            refreshTokenRepository.save(tokenEntity);

            return LoginResponseDto.of(user, tokenDto);

        } catch (HttpClientErrorException e) {
            log.error("Kakao API error: {}", e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.OAUTH_FAILED);
        } catch (Exception e) {
            log.error("Kakao login error", e);
            throw new BusinessException(ErrorCode.OAUTH_FAILED);
        }
    }
}
