package kwh.Petmily_BE.entity;

import jakarta.persistence.*;
import lombok.*;

import java.lang.reflect.Member;
import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private String name;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;

    @Builder
    private User(Long id, String loginId, String password, String name, LocalDate birthDate, String email) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.birthDate = birthDate;

    }

//    @Enumerated(EnumType.STRING)
//    private Role role;
//
//    public enum Role {
//        USER, CAREGIVER
//    }

}
