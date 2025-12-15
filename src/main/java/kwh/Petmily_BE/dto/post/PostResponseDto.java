package kwh.Petmily_BE.dto.post;

import jakarta.persistence.*;
import kwh.Petmily_BE.entity.Pet;
import kwh.Petmily_BE.entity.Post;
import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.enums.PostCategory;
import kwh.Petmily_BE.enums.RequestStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
        User writer,
        Pet pet
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
                post.getWriter(),
                post.getPet()
        );
    }
}
