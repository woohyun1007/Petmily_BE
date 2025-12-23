package kwh.Petmily_BE.domain.user.entity;

import jakarta.persistence.*;
import kwh.Petmily_BE.domain.user.entity.enums.Role;
import kwh.Petmily_BE.global.error.ErrorCode;
import kwh.Petmily_BE.global.error.exception.BusinessException;
import lombok.*;

import java.util.Set;

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

    //역할(Role) 리스트를 별도 테이블에 저장 (N:M 관계를 간소화)
    @ElementCollection(fetch = FetchType.LAZY) // EAGER: 유저 조회 시 role도 항상 같이 가져옴
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles; //Set<Role>을 사용해 다중 역할 지원

    @Builder    // 필요한 필드만 명시적으로 빌더 구성
    private User(String loginId, String password, String email, String nickname, Set<Role> roles) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.roles = roles;
    }

    // DTO 대신 순수 파라미터를 받아서 업데이트
    public void updateProfile(String email, String nickname) {
        if (email != null) this.email = email;
        if (nickname != null) {
            validateUsername(nickname);
            this.nickname = nickname;
        }
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    private void validateUsername(String nickname) {
        if (nickname.length() > 10) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

}
