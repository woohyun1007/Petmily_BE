package kwh.Petmily_BE.domain.pet.service;

import kwh.Petmily_BE.global.error.ErrorCode;
import kwh.Petmily_BE.global.error.exception.BusinessException;
import kwh.Petmily_BE.domain.pet.dto.PetRequestDto;
import kwh.Petmily_BE.domain.pet.dto.PetResponseDto;
import kwh.Petmily_BE.domain.pet.dto.PetUpdateRequestDto;
import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.pet.repository.PetRepository;
import kwh.Petmily_BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    // 반려동물 등록
    public PetResponseDto registerPet(Long userId, PetRequestDto requestDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        //RequestDto -> Pet Entity 변환
        Pet newPet = requestDto.toEntity(owner);

        //Entity -> ReponseDto 변환 후 반환
        return new PetResponseDto(petRepository.save(newPet));
    }

    // 권한 확인(반려동물의 주인이 맞는지 확인)
    private void checkPetOwnership(Pet pet, Long currentUserId) {
        // Pet 엔티티가 User 엔티티에 대한 getter를 가지고 있을 때
        if(!pet.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("이 반려동물 정보에 대한 권한이 없습니다.");
        }
    }

    // 반려동물 목록 조회
    @Transactional(readOnly = true)
    public List<PetResponseDto> getMyPets(Long userId) {
        return petRepository.findByOwner_Id(userId).stream()
                .map(PetResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 반려동물 정보 조회
    @Transactional(readOnly = true)
    public PetResponseDto getPetById(Long userId, Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));

        checkPetOwnership(pet, userId);

        return new PetResponseDto(pet);
    }


    @Transactional
    public PetResponseDto updatePet(Long userId, Long petId, PetUpdateRequestDto requestDto) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));

        // 소유권 확인
        checkPetOwnership(pet, userId);

        pet.updateInfo(requestDto.name(), requestDto.caution(), requestDto.age(), requestDto.image());

        return new PetResponseDto(pet);
    }

    @Transactional
    public void deletePet(Long userId, Long petId) {
        // 반려동물 엔티티 조회
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));

        // 소유권 확인
        checkPetOwnership(pet, userId);
        // 삭제
        petRepository.delete(pet);
    }
}
