package kwh.Petmily_BE.domain.auth.controller;

import kwh.Petmily_BE.domain.auth.dto.LoginRequestDto;
import kwh.Petmily_BE.domain.auth.dto.LoginResponseDto;
import kwh.Petmily_BE.domain.auth.dto.ReissueRequest;
import kwh.Petmily_BE.domain.auth.service.AuthService;
import kwh.Petmily_BE.domain.auth.service.KakaoAuthService;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = authService.login(requestDto.loginId(), requestDto.password());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }
        LoginResponseDto responseDto = authService.getMyInfo(userDetails.getId());

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/reissue")
    public ResponseEntity<LoginResponseDto> reissue(@RequestBody ReissueRequest request) {
        LoginResponseDto responseDto = authService.reissue(request.refreshToken());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getId());
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<LoginResponseDto> kakaoCallback(@RequestParam("code") String code) {
        LoginResponseDto response = kakaoAuthService.loginWithCode(code);
        return ResponseEntity.ok(response);
    }

}
