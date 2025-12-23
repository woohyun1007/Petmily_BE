package kwh.Petmily_BE.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "해당 사용자를 찾을 수 없습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.BAD_REQUEST, "U002", "이미 존재하는 아이디입니다."),

    // Password
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "PW001", "현재 비밀번호가 틀립니다."),
    SAME_AS_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "PW002", "이전 비밀번호와 새 비밀번호가 동일합니다."),

    // Pet
    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "해당 반려동물을 찾을 수 없습니다."),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "PT001", "해당 게시글을 찾을 수 없습니다."),
    PET_REQUIRED_FOR_CARE(HttpStatus.BAD_REQUEST, "PT002", "돌봄 요청엔 반려동물이 필수입니다."),

    // Global
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G001", "잘못된 입력값입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
