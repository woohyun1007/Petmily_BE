package kwh.Petmily_BE.domain.pet.entity;

import jakarta.persistence.*;
import kwh.Petmily_BE.domain.pet.dto.PetUpdateRequestDto;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.pet.entity.enums.Gender;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;    // ex.개, 고양이 등등

    @Column(name = "detail_type")
    private String detail;     // ex.비숑, 말티즈 등등

    private int age;

    private String image;

    @Lob
    private String caution;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @Builder
    private Pet(String name, String type, String detail, int age, String image, String caution, Gender gender, User owner) {
        this.name = name;
        this.type = type;
        this.detail = detail;
        this.age = age;
        this.image = image;
        this.caution = caution;
        this.gender = gender;
        this.owner = owner;
    }

    public void updateInfo(String name, String caution, Integer age, String image) {
        if(name != null) this.name = name;
        if(caution != null) this.caution = caution;
        if(age != null) this.age = age;
        if(image != null) this.image = image;
    }
}
