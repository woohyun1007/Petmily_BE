package kwh.Petmily_BE.domain.auth.controller;

import kwh.Petmily_BE.domain.auth.dto.AuthResponseDto;
import kwh.Petmily_BE.domain.auth.dto.LoginRequestDto;
import kwh.Petmily_BE.domain.auth.dto.LoginResponseDto;
import kwh.Petmily_BE.domain.auth.dto.TokenDto;
import kwh.Petmily_BE.domain.auth.service.AuthService;
import kwh.Petmily_BE.domain.auth.service.KakaoAuthService;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import kwh.Petmily_BE.domain.user.repository.UserRepository;
import kwh.Petmily_BE.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.CookieValue; // 추가
import org.springframework.security.web.csrf.CsrfToken; // CSRF 토큰 주입

import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.Map; // CSRF 응답 맵

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final KakaoAuthService kakaoAuthService;
    private final UserRepository userRepository;

    // 프론트앱 홈으로 리다이렉트할 URL (환경변수 또는 기본값 사용)
    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    // 카카오 앱 키 (로그아웃 URL 구성용)
    @Value("${KAKAO_CLIENT_ID:}")
    private String kakaoClientId;

    // 활성 프로파일을 이용해 운영환경인지 판단 (dev가 아니면 프로덕션으로 간주하는 간단한 로직)
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        AuthResponseDto authResult = authService.login(requestDto.loginId(), requestDto.password());

        if (authResult == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (authResult.tokenInfo() != null) {
            setAuthCookies(response, authResult.tokenInfo());
        }

        // 사용자 정보는 LoginResponseDto로 변환해 반환(토큰은 쿠키로 전달)
        return ResponseEntity.ok(LoginResponseDto.of(authResult.id(), authResult.nickname()));
    }

    @GetMapping
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }
        LoginResponseDto responseDto = authService.getMyInfo(userDetails.getId());


        return ResponseEntity.ok(responseDto);
    }

    // 변경: 리프레시 토큰을 요청 바디가 아니라 HttpOnly 쿠키에서 읽도록 수정
    @PostMapping("/reissue")
    public ResponseEntity<LoginResponseDto> reissue(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        AuthResponseDto authResult = authService.reissue(refreshToken);
        if (authResult == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (authResult.tokenInfo() != null) {
            // reissue 결과로 새 토큰이 발급되었으므로 쿠키를 갱신
            setAuthCookies(response, authResult.tokenInfo());
        }

        return ResponseEntity.ok(LoginResponseDto.of(authResult.id(), authResult.nickname()));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        if (userId != null) {
            authService.logout(userId);
        }

        // 쿠키 삭제: 인증 토큰 쿠키와 CSRF 토큰 쿠키를 모두 만료시킴
        deleteAuthCookies(response);
        deleteCsrfCookie(response);

        // 카카오 로그아웃 URL을 필요로 하는지 확인 (사용자가 카카오로 가입한 경우)
        boolean isKakao = false;
        if (userId != null) {
            isKakao = userRepository.findById(userId).map(User::getKakaoId).isPresent();
        }

        if (isKakao && kakaoClientId != null && !kakaoClientId.isBlank()) {
            String redirect = URLEncoder.encode(frontendUrl, StandardCharsets.UTF_8);
            String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout?client_id=" + kakaoClientId + "&logout_redirect_uri=" + redirect;
            return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다.", "kakaoLogoutUrl", kakaoLogoutUrl));
        }

        return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
    }

    @GetMapping("/kakao/callback")
    public void kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        AuthResponseDto authResult = kakaoAuthService.loginWithCode(code);
        if (authResult != null && authResult.tokenInfo() != null) {
            setAuthCookies(response, authResult.tokenInfo());
        }
        response.sendRedirect(frontendUrl);
    }

    @GetMapping("/csrf")
    public ResponseEntity<Map<String, String>> csrf(CsrfToken csrfToken) {
        if (csrfToken == null) {
            return ResponseEntity.noContent().build();
        }
        Map<String, String> body = Map.of(
                "token", csrfToken.getToken(),
                "headerName", csrfToken.getHeaderName(),
                "parameterName", csrfToken.getParameterName()
        );
        return ResponseEntity.ok(body);
    }

    // CSRF 쿠키 삭제 (CookieCsrfTokenRepository 기본 이름: XSRF-TOKEN)
    private void deleteCsrfCookie(HttpServletResponse response) {
        boolean isProd = "prod".equalsIgnoreCase(activeProfile) || "production".equalsIgnoreCase(activeProfile) || frontendUrl.toLowerCase().startsWith("https");

        ResponseCookie deleteCsrf = ResponseCookie.from("XSRF-TOKEN", "")
                .path("/")
                .maxAge(0)
                .httpOnly(false) // CSRF 토큰 쿠키는 JavaScript에서 읽을 수 있어야 함
                .secure(isProd)
                .sameSite(isProd ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCsrf.toString());
    }

    // 쿠키 세팅 헬퍼
    private void setAuthCookies(HttpServletResponse response, TokenDto tokenInfo) {
        boolean isProd = "prod".equalsIgnoreCase(activeProfile) || "production".equalsIgnoreCase(activeProfile) || frontendUrl.toLowerCase().startsWith("https");

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", tokenInfo.accessToken())
                .path("/")
                .maxAge(60 * 60) // 1시간
                .httpOnly(true)
                .secure(isProd)
                // 개발 환경에서는 SameSite=Lax로 설정해 로컬(HTTP)에서 쿠키가 유실되지 않게 함
                // 운영 환경(HTTPS)에서는 OAuth 리다이렉트가 필요하므로 SameSite=None으로 설정
                .sameSite(isProd ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenInfo.refreshToken())
                .path("/")
                .maxAge(24 * 60 * 60) // 1일
                .httpOnly(true)
                .secure(isProd)
                .sameSite(isProd ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    // 쿠키 삭제 헬퍼
    private void deleteAuthCookies(HttpServletResponse response) {
        boolean isProd = "prod".equalsIgnoreCase(activeProfile) || "production".equalsIgnoreCase(activeProfile) || frontendUrl.toLowerCase().startsWith("https");

        ResponseCookie deleteAccess = ResponseCookie.from("accessToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(isProd)
                .sameSite(isProd ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccess.toString());

        ResponseCookie deleteRefresh = ResponseCookie.from("refreshToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(isProd)
                .sameSite(isProd ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefresh.toString());
    }
}
