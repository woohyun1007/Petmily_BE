package kwh.Petmily_BE.dto.pets;

import kwh.Petmily_BE.entity.Pet;
import kwh.Petmily_BE.entity.User;

public record PetResponseDto(
        Long id,
        String name,
        int age,
        User owner
) {
    public PetResponseDto(Pet pet) {
        this(
                pet.getId(),
                pet.getName(),
                pet.getAge(),
                pet.getOwner()
        );
    }
}
