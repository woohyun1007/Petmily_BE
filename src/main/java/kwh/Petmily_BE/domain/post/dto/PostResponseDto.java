package kwh.Petmily_BE.domain.post.dto;

import kwh.Petmily_BE.domain.post.entity.Post;
import kwh.Petmily_BE.domain.post.entity.enums.PostCategory;
import kwh.Petmily_BE.domain.post.entity.enums.PriceUnit;
import kwh.Petmily_BE.domain.post.entity.enums.RequestStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponseDto(
        Long id,
        String title,
        String content,
        String region,
        PriceUnit priceUnit,
        Long price,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long viewCount,
        PostCategory category,
        RequestStatus status,

        Long writerId,
        String writerNickname,

        Long petId,
        String petName,
        String petImageUrl,

        Double latitude,
        Double longitude
) {
    // Entity -> DTO 변환 시 빌더 활용
    public static PostResponseDto from(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .region(post.getRegion())
                .priceUnit(post.getPriceUnit())
                .price(post.getPrice())

                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .viewCount(post.getViewCount())
                .category(post.getCategory())
                .status(post.getStatus())

                .writerId(post.getWriter().getId())
                .writerNickname(post.getWriter().getNickname())

                .petId(post.getPet() != null ? post.getPet().getId() : null)
                .petName(post.getPet() != null ? post.getPet().getName() : null)
                .petImageUrl(post.getPet() != null ? post.getPet().getImageUrl() : null)

                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .build();
    }
}
