package kwh.Petmily_BE.entity;

import jakarta.persistence.*;
import kwh.Petmily_BE.dto.pets.PetUpdateRequestDto;
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

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    public void updateFromDto(PetUpdateRequestDto requestDto) {
        this.caution = requestDto.caution();
        this.age = requestDto.age();
        this.image = requestDto.image();
    }
}
