package kwh.Petmily_BE.domain.user.dto;

import jakarta.validation.constraints.*;

public record UserUpdateRequestDto(
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @Size(min = 2, max = 10, message = "이름은 2~10자여야 합니다.")
        String nickname
) {
}
