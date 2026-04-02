package kwh.Petmily_BE.domain.pet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.pet.entity.enums.Gender;
import kwh.Petmily_BE.domain.pet.entity.enums.Type;
import kwh.Petmily_BE.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

public record PetRequestDto(

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotNull(message = "개체 선택은 필수입니다.")
        Type type,   // ex.개, 고양이 등등

        String breed,   // ex.비숑, 말티즈 등등

        @NotNull@Min(value = 0, message = "나이는 0세 이상이어야 합니다.")
        Integer age,

        @NotNull(message = "사진은 필수입니다.")
        MultipartFile image,

        @Size(max = 300, message = "최대 300자 제한입니다.")
        String caution,

        @NotNull(message = "성별은 필수입니다.")
        Gender gender
) {
        // DTO -> Entity 변환 시 빌더 사용
        public Pet toEntity(User owner, String imageUrl) {
                return Pet.builder()
                        .name(name)
                        .type(type)
                        .breed(breed)
                        .age(age == null ? 0 : age)
                        .imageUrl(imageUrl)
                        .caution(caution)
                        .gender(gender)
                        .owner(owner)
                        .build();
        }
}
