package kwh.Petmily_BE.domain.post.dto;

import kwh.Petmily_BE.domain.post.entity.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponseDto(
        Long id,
        String content,
        String nickname,
        Long writerId,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    // Entity -> DTO 변환 시 빌더 활용
    public static CommentResponseDto from(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getWriter().getNickname())
                .writerId(comment.getWriter().getId())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }
}
