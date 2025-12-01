package kwh.Petmily_BE.dto;

import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.enums.Role;
import java.util.Set;

// 로그인 성공 응답 DTO
public record LoginResponseDto(
        String token,       // 💡 발급된 JWT 토큰
        String tokenType,   // 토큰 타입 (보통 "Bearer"로 고정)
        Long id,            // 사용자 고유 ID
        String username,    // 사용자 실명
        Set<Role> roles     // 사용자 역할 목록
) {
    public static LoginResponseDto of(String token, User user) {
        return new LoginResponseDto(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getRoles()
        );
    }
}