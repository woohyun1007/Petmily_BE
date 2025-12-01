package kwh.Petmily_BE.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kwh.Petmily_BE.enums.Role;

public record JoinRequestDto(

        @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,

        @NotBlank(message = "아이디는 필수입니다.")
    String loginId,

        @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    String password,

        @NotBlank(message = "이름은 필수입니다.")
    String username,

        @NotNull(message = "역할(OWNER/SITTER)은 필수입니다.")
        Role roles // 이전에 논의했던 Role Enum (OWNER, SITTER)
    ){
}
