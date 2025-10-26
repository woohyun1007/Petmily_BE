package kwh.Petmily_BE.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@Builder
@Table(name = "User")
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
    private String username;

    @ElementCollection(fetch = FetchType.EAGER) // EAGER: 유저 조회 시 role도 항상 같이 가져옴
    @Enumerated(EnumType.STRING)
    private Set<Role> role = new HashSet<>();

    public enum Role {
        OWNER, SITTER, ADMIN
    }

    @Builder
    private User(Long id, String loginId, String password, String username, String email, Set<Role> role) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.username = username;
        this.role = role;
    }

}
