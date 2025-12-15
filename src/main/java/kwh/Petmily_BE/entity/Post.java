package kwh.Petmily_BE.entity;

import jakarta.persistence.*;
import kwh.Petmily_BE.dto.post.PostRequestDto;
import kwh.Petmily_BE.dto.post.PostUpdateRequestDto;
import kwh.Petmily_BE.enums.PostCategory;
import kwh.Petmily_BE.enums.RequestStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue
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

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory category;

    @ElementCollection(fetch = FetchType.EAGER) // EAGER: 요청 조회 시 status도 항상 같이 가져옴
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    public int incrementViewCount() {
        return viewCount++;
    }

    public void update(PostUpdateRequestDto requestDto, Pet pet) {
        this.title = requestDto.title();
        this.content = requestDto.content();;
        this.region = requestDto.region();
        this.price = requestDto.price();
        this.updatedAt = requestDto.updatedAt();
        this.status = requestDto.status();
        this.pet = pet;
    }
}
