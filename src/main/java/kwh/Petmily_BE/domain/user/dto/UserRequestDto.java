package kwh.Petmily_BE.domain.user.dto;


import jakarta.validation.constraints.*;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.user.entity.enums.Role;

import java.util.Collections;

public record UserRequestDto(

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "아이디는 필수입니다.")
        String loginId,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "이름은 필수입니다.")
        String nickname,

        @NotNull(message = "역할(OWNER/SITTER)은 필수입니다.")
        Role roles      // Set<Role> 대신 단일 Role로 받아서 안전하게 처리
){
    // DTO -> Entity 변환 시 빌더 사용
    public User toEntity(String encodedPassword) {
        return User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .email(email)
                .nickname(nickname)
                .roles(Collections.singleton(roles))
                .build();
    }
}
