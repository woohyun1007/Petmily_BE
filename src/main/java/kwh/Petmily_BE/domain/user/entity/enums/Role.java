package kwh.Petmily_BE.domain.user.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    OWNER("ROLE_OWNER", "반려동물 주인"),
    SITTER("ROLE_SITTER", "펫시터"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
