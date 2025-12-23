package kwh.Petmily_BE.domain.pet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kwh.Petmily_BE.domain.pet.entity.enums.Gender;

public record PetUpdateRequestDto(
        String name,

        int age,

        String image,

        @Size(max = 300, message = "최대 300자 제한입니다.")
        String caution
) {
}
