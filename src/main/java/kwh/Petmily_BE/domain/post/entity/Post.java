package kwh.Petmily_BE.domain.post.entity;

import jakarta.persistence.*;
import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.post.dto.PostUpdateRequestDto;
import kwh.Petmily_BE.domain.post.entity.enums.PostCategory;
import kwh.Petmily_BE.domain.post.entity.enums.RequestStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 100)
    private String region;

    private int price;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private int viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Builder
    public Post(String title, String content, String region, int price,
                PostCategory category, RequestStatus status, User writer, Pet pet) {
        this.title = title;
        this.content = content;
        this.region = region;
        this.price = price;
        this.category = category;
        this.status = status;
        this.writer = writer;
        this.pet = pet;
    }

    public int incrementViewCount() {
        return viewCount++;
    }

    public void update(String title, String content, String region, int price, RequestStatus status, Pet pet) {
        if(title != null) this.title = title;
        if(content!= null) this.content = content;
        if(region != null) this.region = region;
        if(price != 0) this.price = price;
        if(status != null) this.status = status;
        if(pet != null) this.pet = pet;
    }
}
