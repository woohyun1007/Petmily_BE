package kwh.Petmily_BE.domain.pet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kwh.Petmily_BE.domain.pet.dto.PetRequestDto;
import kwh.Petmily_BE.domain.pet.dto.PetResponseDto;
import kwh.Petmily_BE.domain.pet.dto.PetUpdateRequestDto;
import kwh.Petmily_BE.domain.pet.service.PetService;
import kwh.Petmily_BE.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Tag(name = "Pet API", description = "반려동물 관련 API")
public class PetController {

    private final PetService petService;

    @Operation(summary = "반려동물 등록", description = "새로운 반려동물을 등록합니다.")
    @PostMapping
    public ResponseEntity<PetResponseDto> registerPet(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid@RequestBody PetRequestDto requestDto) {
        PetResponseDto responseDto = petService.registerPet(userDetails.getId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "반려동물 목록 조회", description = "반려동물 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<PetResponseDto>> getMyPets(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PetResponseDto> pets = petService.getMyPets(userDetails.getId());
        return ResponseEntity.ok(pets);
    }

    @Operation(summary = "특정 반려동물 조회", description = "해당 반려동물 조회합니다.")
    @GetMapping("/{petId}")
    public ResponseEntity<PetResponseDto> getPetInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("petId") Long petId) {
        PetResponseDto responseDto = petService.getPetById(userDetails.getId(), petId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "반려동물 정보 변경", description = "해당 반려동물 정보를 변경합니다.")
    @PatchMapping("/{petId}")
    public ResponseEntity<PetResponseDto> updatePet(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("petId") Long petId, @Valid@RequestBody PetUpdateRequestDto requestDto) {
        PetResponseDto responseDto = petService.updatePet(userDetails.getId(), petId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "반려동물 정보 삭제", description = "해당 반려동물 정보를 삭제합니다.")
    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("petId") Long petId) {
        petService.deletePet(userDetails.getId(), petId);
        return ResponseEntity.noContent().build();
    }

}
