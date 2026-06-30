package kwh.Petmily_BE.domain.pet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kwh.Petmily_BE.domain.pet.entity.enums.Type;
import org.springframework.web.multipart.MultipartFile;

public record PetUpdateRequestDto(
        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 10, message = "이름은 10글자 이하로 입력해주세요.")
        String name,

        @NotNull(message = "개체 선택은 필수입니다.")
        Type type,

        String breed,

        @NotNull(message = "나이는 필수입니다.")
        @Min(value = 0, message = "나이는 0세 이상이어야 합니다.")
        Integer age,

        @NotNull(message = "사진은 필수입니다.")
        MultipartFile image,

        @Size(max = 100, message = "주의사항은 100글자 이하로 입력해주세요.")
        String caution
) {
}
