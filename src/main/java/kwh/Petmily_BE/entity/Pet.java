package kwh.Petmily_BE.entity;

import jakarta.persistence.*;
import kwh.Petmily_BE.enums.Gender;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;    // ex.개, 고양이 등등
    private String detail_type;     // ex.비숑, 말티즈 등등
    private int age;
    private String image;
    private String caution;

    @ElementCollection(fetch = FetchType.EAGER) // EAGER: 요청 조회 시 status도 항상 같이 가져옴
    @Enumerated(EnumType.STRING)
    private Gender genders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;
}
