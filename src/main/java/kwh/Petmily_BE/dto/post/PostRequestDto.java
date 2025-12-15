package kwh.Petmily_BE.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kwh.Petmily_BE.enums.PostCategory;
import kwh.Petmily_BE.enums.RequestStatus;

public record PostRequestDto(
        @NotBlank
        String title,
        @NotBlank
        String content,
        String region,
        int price,
        @NotNull
        PostCategory category,

        @NotNull
        RequestStatus status,

        Long petId
) {
}
