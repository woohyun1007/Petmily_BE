package kwh.Petmily_BE.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 10)
    private String nickname;

    // 카카오 연동용 ID (null 허용)
    @Column(nullable = true, unique = true)
    private Long kakaoId;

    @Builder    // 필요한 필드만 명시적으로 빌더 구성
    private User(String loginId, String password, String email, String nickname, Long kakaoId) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.kakaoId = kakaoId;
    }

    // DTO 대신 순수 파라미터를 받아서 업데이트
    public void updateProfile(String nickname) {
        if (nickname != null) {
//            validateUsername(nickname);
            this.nickname = nickname;
        }
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 카카오 ID 설정
    public void setKakaoId(Long kakaoId) {
        this.kakaoId = kakaoId;
    }

//    private void validateUsername(String nickname) {
//        if (nickname.length() > 10) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
//    }

}
