package kwh.Petmily_BE.domain.pet.repository;

import kwh.Petmily_BE.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Boolean existsByOwner_Id(Long ownerId);
    List<Pet> findByOwner_Id(Long ownerId);
    Optional<Pet> findByIdAndOwnerId(Long Id, Long ownerId);

    // 특정 소유자가 가진 모든 펫을 일괄 삭제 (회원 탈퇴 시 사용)
    @Modifying(clearAutomatically = true)
    @Query("delete from Pet p where p.owner.id = :ownerId")
    void deleteAllByOwnerId(@Param("ownerId") Long ownerId);
}
