package kwh.Petmily_BE.domain.user.dto;

import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.user.entity.enums.Role;
import java.util.Set;

// 로그인 성공 응답 DTO
public record LoginResponseDto(
        String token,       // 💡 발급된 JWT 토큰
        String tokenType,   // 토큰 타입 (보통 "Bearer"로 고정)
        Long id,            // 사용자 고유 ID
        String nickname,    // 사용자 닉네임
        Set<Role> roles     // 사용자 역할 목록
) {
    public static LoginResponseDto of(String token, User user) {
        return new LoginResponseDto(
                token,
                "Bearer",
                user.getId(),
                user.getNickname(),
                user.getRoles()
        );
    }
}