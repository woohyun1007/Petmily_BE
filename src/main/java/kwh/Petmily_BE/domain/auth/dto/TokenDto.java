package kwh.Petmily_BE.domain.auth.dto;

import lombok.Builder;

@Builder
public record TokenDto(
        String grantType,
        String accessToken,
        String refreshToken
) {
}
