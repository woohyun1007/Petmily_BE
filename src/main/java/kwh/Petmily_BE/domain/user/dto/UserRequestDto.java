package kwh.Petmily_BE.domain.user.dto;


import jakarta.validation.constraints.*;
import kwh.Petmily_BE.domain.user.entity.User;

public record UserRequestDto(

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "아이디는 필수입니다.")
        @Size(min = 2, max = 20, message = "아이디는 2글자 이상 20글자 이하로 입력해주세요.")
        String loginId,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
        String nickname

){
    // DTO -> Entity 변환 시 빌더 사용
    public User toEntity(String encodedPassword) {
        return User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .email(email)
                .nickname(nickname)
                .build();
    }
}
