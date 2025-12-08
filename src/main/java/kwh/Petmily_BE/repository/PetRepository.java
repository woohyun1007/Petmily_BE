package kwh.Petmily_BE.repository;

import kwh.Petmily_BE.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Boolean existsOwnerId(String owner);
    List<Pet> findByOwnerId(Long owner);
}
