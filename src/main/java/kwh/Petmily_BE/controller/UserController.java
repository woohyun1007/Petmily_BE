package kwh.Petmily_BE.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kwh.Petmily_BE.dto.users.JoinRequestDto;
import kwh.Petmily_BE.dto.users.JoinResponseDto;
import kwh.Petmily_BE.dto.users.UserUpdateRequestDto;
import kwh.Petmily_BE.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "회원 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<JoinResponseDto> signUp(@Valid @RequestBody JoinRequestDto requestDto) {
        JoinResponseDto responseDto = userService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "회원 정보 조회", description = "회원 정보를 조회합니다.")
    @GetMapping("/info/{id}")
    public ResponseEntity<JoinResponseDto> getMyInfo() {
        JoinResponseDto responseDto = userService.getMyInfo();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "회원 정보 변경", description = "회원 정보를 변경합니다.")
    @PatchMapping("/update/{id}")
    public ResponseEntity<JoinResponseDto> updateUser(@Valid @RequestBody UserUpdateRequestDto requestDto) {
        JoinResponseDto responseDto = userService.updateMyInfo(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "회원탈퇴", description = "회원 탈퇴를 진행합니다.")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser() {
        userService.deleteMyInfo();
        return ResponseEntity.noContent().build();
    }

}
