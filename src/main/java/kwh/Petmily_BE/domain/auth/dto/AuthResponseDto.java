package kwh.Petmily_BE.domain.auth.dto;

import kwh.Petmily_BE.domain.user.entity.User;
import lombok.Builder;

@Builder
public record AuthResponseDto(
        Long id,
        String nickname,
        TokenDto tokenInfo
) {
    public static AuthResponseDto from(User user, TokenDto tokenDto) {
        return AuthResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .tokenInfo(tokenDto)
                .build();
    }
}
