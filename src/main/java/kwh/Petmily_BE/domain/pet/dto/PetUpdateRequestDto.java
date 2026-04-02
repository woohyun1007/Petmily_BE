package kwh.Petmily_BE.domain.pet.dto;

import jakarta.validation.constraints.Size;
import kwh.Petmily_BE.domain.pet.entity.enums.Type;
import org.springframework.web.multipart.MultipartFile;

public record PetUpdateRequestDto(
        String name,

        Type type,

        String breed,

        Integer age,

        MultipartFile image,

        @Size(max = 300, message = "최대 300자 제한입니다.")
        String caution
) {
}
