package kwh.Petmily_BE.dto.pets;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.enums.Gender;

public record PetRequestDto(

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "개체 선택은 필수입니다.")
        String type,   // ex.개, 고양이 등등

        @NotBlank(message = "품종 선택은 필수입니다.")
        String detail_type,   // ex.비숑, 말티즈 등등

        int age,

        @NotBlank(message = "사진은 필수입니다.")
        String image,

        @Size(max = 300, message = "최대 300자 제한입니다.")
        String caution,

        @NotBlank(message = "성별은 필수입니다.")
        Gender genders,

        @NotBlank(message = "주인명은 필수입니다.")
        User owner
) {
}
