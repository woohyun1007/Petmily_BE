package kwh.Petmily_BE.domain.post.dto;

import kwh.Petmily_BE.domain.post.entity.enums.PostCategory;
import kwh.Petmily_BE.domain.post.entity.enums.RequestStatus;

public record PostUpdateRequestDto(
        String title,
        String content,
        String region,
        int price,
        RequestStatus status,
        PostCategory category,
        Long petId
) {
}
