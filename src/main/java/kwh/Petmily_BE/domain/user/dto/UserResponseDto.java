package kwh.Petmily_BE.domain.user.dto;

import kwh.Petmily_BE.domain.user.entity.enums.Role;
import kwh.Petmily_BE.domain.user.entity.User;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserResponseDto(
        Long id,
        String nickname,
        String email,
        String loginId,
        String password,
        Set<Role> roles
) {
    // Entity -> DTO 변환 시 빌더 활용
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .loginId(user.getLoginId())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();

    }
}
