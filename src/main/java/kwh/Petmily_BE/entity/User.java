package kwh.Petmily_BE.entity;

import jakarta.persistence.*;
import kwh.Petmily_BE.dto.users.UserUpdateRequestDto;
import kwh.Petmily_BE.enums.Role;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Builder
@Table(name = "Users")
@AllArgsConstructor
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

    //역할(Role) 리스트를 별도 테이블에 저장 (N:M 관계를 간소화)
    @ElementCollection(fetch = FetchType.EAGER) // EAGER: 유저 조회 시 role도 항상 같이 가져옴
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles; //Set<Role>을 사용해 다중 역할 지원

    public void updateFromDto(UserUpdateRequestDto requestDto) {
        if(requestDto.email() != null) {
            this.email = requestDto.email();
        }
        if(requestDto.username() != null) {
            this.username = requestDto.username();
        }
   }

   public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
   }

}
