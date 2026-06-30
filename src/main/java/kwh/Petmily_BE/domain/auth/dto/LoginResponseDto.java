package kwh.Petmily_BE.domain.auth.dto;

import kwh.Petmily_BE.domain.user.entity.User;
import lombok.Builder;

@Builder
public record LoginResponseDto(
        Long id,            // 사용자 고유 ID
        String nickname     // 사용자 닉네임
) {
    // Entity -> DTO 변환 시 빌더 활용
    public static LoginResponseDto of(User user) {
        return LoginResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    // id, nickname으로 직접 생성하는 편의 팩토리 메서드 추가
    public static LoginResponseDto of(Long id, String nickname) {
        return LoginResponseDto.builder()
                .id(id)
                .nickname(nickname)
                .build();
    }
}