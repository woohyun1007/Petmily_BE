package kwh.Petmily_BE.domain.post.entity;

import jakarta.persistence.*;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.global.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @Builder
    public Comment(String content, Post post, User writer) {
        this.content = content;
        this.post = post;
        this.writer = writer;
    }

    public void update(String content) {
        if(content != null && !content.trim().isEmpty()) this.content = content;
    }
}
