package kwh.Petmily_BE.domain.post.dto;

import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.post.entity.Post;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.post.entity.enums.PostCategory;
import kwh.Petmily_BE.domain.post.entity.enums.RequestStatus;

import java.time.LocalDateTime;

public record PostResponseDto(
        Long id,
        String title,
        String content,
        String region,
        int price,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int viewCount,
        PostCategory category,
        RequestStatus status,

        Long writerId,
        String writerNickname,

        Long petId,
        String petName,
        String petImage
) {
    public PostResponseDto(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getRegion(),
                post.getPrice(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getViewCount(),
                post.getCategory(),
                post.getStatus(),

                post.getWriter().getId(),
                post.getWriter().getNickname(),

                post.getPet() != null ? post.getPet().getId() : null,
                post.getPet() != null ? post.getPet().getName() : null,
                post.getPet() != null ? post.getPet().getImage() : null
        );
    }
}
