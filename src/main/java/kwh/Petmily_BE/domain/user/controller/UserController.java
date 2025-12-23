package kwh.Petmily_BE.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kwh.Petmily_BE.domain.user.dto.*;
import kwh.Petmily_BE.domain.user.service.UserService;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "회원 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping
    public ResponseEntity<UserResponseDto> signUp(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto responseDto = userService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "내 정보 조회")
    @GetMapping
    public ResponseEntity<UserResponseDto> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponseDto responseDto = userService.getMyInfo(userDetails.getId());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "정보 수정")
    @PatchMapping
    public ResponseEntity<UserResponseDto> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody UserUpdateRequestDto requestDto) {
        UserResponseDto responseDto = userService.updateMyInfo(userDetails.getId(), requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "비밀번호 변경")
    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody PasswordUpdateRequestDto requestDto) {
        userService.updatePassword(userDetails.getId(), requestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteMyInfo(userDetails.getId());
        return ResponseEntity.noContent().build();
    }

}
