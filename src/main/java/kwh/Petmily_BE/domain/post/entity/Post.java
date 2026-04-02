package kwh.Petmily_BE.domain.post.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.post.entity.enums.PriceUnit;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.post.entity.enums.PostCategory;
import kwh.Petmily_BE.domain.post.entity.enums.RequestStatus;
import kwh.Petmily_BE.global.BaseTimeEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 100)
    private String region;

    @Enumerated(EnumType.STRING)
    private PriceUnit priceUnit;
    private Long price;

    // 위도/경도 추가
    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "BIGINT default 0")
    private Long viewCount = 0L;

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
    @JoinColumn(name = "pet_id")
    private Pet pet;

    // 댓글 연관관계: Post 삭제 시 댓글도 함께 삭제되도록 cascade/remove와 orphanRemoval 사용
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(String title, String content, String region, Long price, PriceUnit priceUnit,
                PostCategory category, RequestStatus status, Double latitude, Double longitude, User writer, Pet pet) {
        this.title = title;
        this.content = content;
        this.region = region;
        this.priceUnit = priceUnit;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.status = status;
        this.writer = writer;
        this.pet = pet;
    }

    public void update(String title, String content, String region, PriceUnit priceUnit, Long price, RequestStatus status, Double latitude, Double longitude, Pet pet) {
        if(title != null) this.title = title;
        if(content != null) this.content = content;
        if(region != null) this.region = region;
        if(priceUnit != null) this.priceUnit = priceUnit;
        if(price != null) this.price = price;
        if(status != null) this.status = status;
        if(latitude != null) this.latitude = latitude;
        if(longitude != null) this.longitude = longitude;
        if(pet != null) this.pet = pet;
    }
}
