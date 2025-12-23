package kwh.Petmily_BE.domain.pet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.pet.entity.enums.Gender;
import kwh.Petmily_BE.domain.user.entity.User;

import java.util.Collections;

public record PetRequestDto(

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "개체 선택은 필수입니다.")
        String type,   // ex.개, 고양이 등등

        String detail,   // ex.비숑, 말티즈 등등

        @Min(value = 0, message = "나이는 0세 이상이어야 합니다.")
        int age,

        @NotBlank(message = "사진은 필수입니다.")
        String image,

        @Size(max = 300, message = "최대 300자 제한입니다.")
        String caution,

        @NotNull(message = "성별은 필수입니다.")
        Gender gender
) {
        // DTO -> Entity 변환 메소드
        public Pet toEntity(User owner) {
                return Pet.builder()
                        .name(this.name)
                        .type(this.type)
                        .detail(this.detail)
                        .age(this.age)
                        .image(this.image)
                        .caution(this.caution)
                        .gender(this.gender)
                        .owner(owner)
                        .build();
        }
}
