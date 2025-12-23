package kwh.Petmily_BE.domain.pet.dto;

import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.pet.entity.enums.Gender;

public record PetResponseDto(
        Long id,
        String name,
        String type,
        String detailType,
        int age,
        String image,
        String caution,
        Gender gender,
        Long ownerId,
        String ownerUsername
) {
    public PetResponseDto(Pet pet) {
        this(
                pet.getId(),
                pet.getName(),
                pet.getType(),
                pet.getDetail(),
                pet.getAge(),
                pet.getImage(),
                pet.getCaution(),
                pet.getGender(),
                pet.getOwner().getId(),
                pet.getOwner().getNickname()
        );
    }
}
