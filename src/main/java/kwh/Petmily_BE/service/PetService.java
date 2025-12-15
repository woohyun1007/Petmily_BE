package kwh.Petmily_BE.service;

import kwh.Petmily_BE.config.SecurityUtil;
import kwh.Petmily_BE.dto.pets.PetRequestDto;
import kwh.Petmily_BE.dto.pets.PetResponseDto;
import kwh.Petmily_BE.dto.pets.PetUpdateRequestDto;
import kwh.Petmily_BE.entity.Pet;
import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.repository.PetRepository;
import kwh.Petmily_BE.repository.UserRepository;
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
    public PetResponseDto registerPet(PetRequestDto requestDto) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        User owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        //RequestDto -> Pet Entity 변환
        Pet newPet = Pet.builder()
                .name(requestDto.name())
                .type(requestDto.type())
                .detail_type(requestDto.detail_type())
                .age(requestDto.age())
                .image(requestDto.image())
                .gender(requestDto.gender())
                .caution(requestDto.caution())
                .owner(owner)
                .build();

        //DB에 저장
        Pet savedPet = petRepository.save(newPet);

        //Entity -> ReponseDto 변환 후 반환
        return new PetResponseDto(savedPet);
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
    public List<PetResponseDto> getMyPets() {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 현재 사용자 ID를 기반으로 반려동물 목록 조회
        List<Pet> pets = petRepository.findByOwner_Id(currentUserId);

        // Entity List를 DTO List로 변환
        return pets.stream()
                .map(PetResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 반려동물 정보 조회
    @Transactional(readOnly = true)
    public PetResponseDto getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려동물을 찾을 수 없습니다."));

        checkPetOwnership(pet, SecurityUtil.getCurrentUserId());
        return new PetResponseDto(pet);
    }


    @Transactional
    public PetResponseDto updatePet(Long id, PetUpdateRequestDto requestDto) {
        // Pet 엔티티 조회 (checkPetOwnership에서 예외를 던질 수 있으르모 다시 조회)
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려동물을 찾을 수 없습니다."));

        // 소유권 확인
        checkPetOwnership(pet, SecurityUtil.getCurrentUserId());

        pet.updateFromDto(requestDto);

        return new PetResponseDto(pet);
    }

    @Transactional
    public void deletePet(Long id) {
        // 반려동물 엔티티 조회
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려동물을 찾을 수 없습니다."));

        // 소유권 확인
        checkPetOwnership(pet, SecurityUtil.getCurrentUserId());

        // 삭제
        petRepository.delete(pet);
    }
}
