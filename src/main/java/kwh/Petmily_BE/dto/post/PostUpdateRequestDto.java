package kwh.Petmily_BE.dto.post;

import kwh.Petmily_BE.entity.Pet;
import kwh.Petmily_BE.enums.RequestStatus;

import java.time.LocalDateTime;

public record PostUpdateRequestDto(
        String title,
        String content,
        String region,
        int price,
        LocalDateTime updatedAt,
        RequestStatus status,
        Pet pet
) {
}
