package kwh.Petmily_BE.domain.post.dto;

import kwh.Petmily_BE.domain.post.entity.Comment;
import kwh.Petmily_BE.domain.post.entity.Post;
import kwh.Petmily_BE.domain.user.entity.User;

public record CommentRequestDto(
        String content
) {
    // DTO -> Entity 변환 시 빌더 사용
    public Comment toEntity(Post post, User writer) {
        return Comment.builder()
                .content(content)
                .post(post)
                .writer(writer)
                .build();
    }
}
