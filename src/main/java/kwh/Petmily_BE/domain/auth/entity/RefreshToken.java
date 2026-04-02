package kwh.Petmily_BE.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId; // 유저 식별자

    @Column(nullable = false)
    private String token; // 리프레시 토큰 값

    public RefreshToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    // 토큰 갱신을 위한 메서드
    public void updateToken(String newToken) {
        this.token = newToken;
    }
}
