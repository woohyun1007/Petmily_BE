package kwh.Petmily_BE.domain.pet.dto;

import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.pet.entity.enums.Gender;
import kwh.Petmily_BE.domain.pet.entity.enums.Type;
import lombok.Builder;

@Builder
public record PetResponseDto(
        Long id,
        String name,
        Type type,
        String breed,
        Integer age,
        String imageUrl,
        String caution,
        Gender gender,
        Long ownerId,
        String ownerUsername
) {
    // Entity -> DTO 변환 시 빌더 활용
    public static PetResponseDto from(Pet pet) {
        return PetResponseDto.builder()
                .id(pet.getId())
                .name(pet.getName())
                .type(pet.getType())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .imageUrl(pet.getImageUrl())
                .caution(pet.getCaution())
                .gender(pet.getGender())
                .ownerId(pet.getOwner().getId())
                .ownerUsername(pet.getOwner().getNickname())
                .build();
    }
}
