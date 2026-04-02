package kwh.Petmily_BE.domain.pet.entity;

import jakarta.persistence.*;
import kwh.Petmily_BE.domain.pet.entity.enums.Type;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;    // ex.개, 고양이 등등

    private String breed;     // ex.비숑, 말티즈 등등

    private Integer age;

    private String imageUrl;

    @Lob
    private String caution;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Builder
    private Pet(String name, Type type, String breed, Integer age, String imageUrl, String caution, Gender gender, User owner) {
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.age = age;
        this.imageUrl = imageUrl;
        this.caution = caution;
        this.gender = gender;
        this.owner = owner;
    }

    public void updateInfo(String name, Type type, String breed, Integer age, String imageUrl, String caution) {
        if (name != null) this.name = name;
        if (type != null) this.type = type;
        if (breed != null) this.breed = breed;
        if (age != null) this.age = age;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (caution != null) this.caution = caution;
    }
}
