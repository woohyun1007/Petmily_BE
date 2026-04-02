package kwh.Petmily_BE.domain.auth.dto;

import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.user.entity.enums.Role;
import lombok.Builder;

import java.util.Set;

@Builder
public record LoginResponseDto(
        Long id,            // 사용자 고유 ID
        String nickname,    // 사용자 닉네임
        Set<Role> roles,    // 사용자 역할 목록
        TokenDto tokenInfo
) {
    // Entity -> DTO 변환 시 빌더 활용
    public static LoginResponseDto of(User user, TokenDto tokenDto) {
        return LoginResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .roles(user.getRoles())
                .tokenInfo(tokenDto)
                .build();
    }
}