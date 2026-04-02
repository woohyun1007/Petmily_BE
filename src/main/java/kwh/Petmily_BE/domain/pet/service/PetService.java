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
import kwh.Petmily_BE.global.file.FileService;
import kwh.Petmily_BE.domain.post.repository.PostRepository;
import kwh.Petmily_BE.domain.post.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final EntityManager em;

    // 반려동물 등록
    @Transactional
    public PetResponseDto registerPet(Long userId, PetRequestDto requestDto) {
        String imageUrl = null;
        if (requestDto.image() != null && !requestDto.image().isEmpty()) {
            // 잘못된 변수(image)를 requestDto.image()로 변경
            imageUrl = fileService.storeFile(requestDto.image());
        }
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        //RequestDto -> Pet Entity 변환
        Pet newPet = requestDto.toEntity(owner, imageUrl);
        Pet savedPet = petRepository.save(newPet);
        //Entity -> ReponseDto 변환 후 반환
        return PetResponseDto.from(savedPet);
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
                .map(PetResponseDto::from)
                .collect(Collectors.toList());
    }

    // 특정 반려동물 정보 조회
    @Transactional(readOnly = true)
    public PetResponseDto getPetById(Long userId, Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));

        checkPetOwnership(pet, userId);

        return PetResponseDto.from(pet);
    }


    @Transactional
    public PetResponseDto updatePet(Long userId, Long petId, PetUpdateRequestDto requestDto) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));

        // 소유권 확인
        checkPetOwnership(pet, userId);

        // 이미지가 새로 들어오면 저장하고 URL을 업데이트에 사용
        String newImageUrl = null;
        if (requestDto.image() != null && !requestDto.image().isEmpty()) {
            // 기존 이미지 삭제 (있다면)
            fileService.deleteFile(pet.getImageUrl());
            newImageUrl = fileService.storeFile(requestDto.image());
        }

        pet.updateInfo(requestDto.name(), requestDto.type(), requestDto.breed(), requestDto.age(), newImageUrl, requestDto.caution());

        return PetResponseDto.from(pet);
    }

    @Transactional
    public void deletePet(Long userId, Long petId) {
        // 반려동물 엔티티 조회
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));

        // 소유권 확인
        checkPetOwnership(pet, userId);

        // 해당 펫에 연결된 게시글 id들을 조회
        List<Long> postIds = postRepository.findIdsByPetId(petId);

        if (!postIds.isEmpty()) {
            // 댓글을 배치 삭제
            commentRepository.deleteAllByPostIds(postIds);
            // 게시글을 배치 삭제
            postRepository.deleteAllByPetId(petId);

            // 영속성 컨텍스트 동기화
            em.flush();
            em.clear();
        }

        // 업로드된 이미지 파일 삭제
        fileService.deleteFile(pet.getImageUrl());

        // 펫 삭제
        petRepository.delete(pet);
    }
}
