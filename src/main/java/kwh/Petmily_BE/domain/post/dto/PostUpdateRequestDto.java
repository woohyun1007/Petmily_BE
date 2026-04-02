package kwh.Petmily_BE.domain.post.dto;

import kwh.Petmily_BE.domain.post.entity.enums.PostCategory;
import kwh.Petmily_BE.domain.post.entity.enums.PriceUnit;
import kwh.Petmily_BE.domain.post.entity.enums.RequestStatus;

public record PostUpdateRequestDto(
        String title,
        String content,
        String region,
        PriceUnit priceUnit,
        Long price,
        RequestStatus status,
        PostCategory category,
        String petName,
        Long petId,
        Double latitude,
        Double longitude
) {
}
