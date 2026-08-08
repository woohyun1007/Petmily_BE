package kwh.Petmily_BE.domain.auth.controller;

import jakarta.validation.Valid;
import kwh.Petmily_BE.domain.auth.dto.EmailVerificationConfirmRequestDto;
import kwh.Petmily_BE.domain.auth.dto.EmailVerificationSendRequestDto;
import kwh.Petmily_BE.domain.auth.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendVerificationCode(@Valid @RequestBody EmailVerificationSendRequestDto requestDto) {
        emailVerificationService.sendVerificationCode(requestDto.email());
        return ResponseEntity.ok(Map.of("message", "인증번호를 전송했습니다."));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyCode(@Valid @RequestBody EmailVerificationConfirmRequestDto requestDto) {
        emailVerificationService.verifyCode(requestDto.email(), requestDto.code());
        return ResponseEntity.ok(Map.of("message", "이메일 인증이 완료되었습니다."));
    }
}
