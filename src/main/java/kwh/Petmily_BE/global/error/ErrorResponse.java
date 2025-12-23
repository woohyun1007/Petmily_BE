package kwh.Petmily_BE.global.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private final String code;    // 기획/개발자가 정한 에러 코드 (예: U001)
    private final String message; // 에러 메시지
    private final int status;    // HTTP 상태 코드 (예: 404)

    // 에러 발생 시 ErrorCode를 받아서 생성
    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .status(errorCode.getStatus().value())
                .build();
    }
}
