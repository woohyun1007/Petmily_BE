package kwh.Petmily_BE.domain.pet.repository;

import kwh.Petmily_BE.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Boolean existsByOwner_Id(Long ownerId);
    List<Pet> findByOwner_Id(Long ownerId);
}
