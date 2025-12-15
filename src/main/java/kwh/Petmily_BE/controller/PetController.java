package kwh.Petmily_BE.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kwh.Petmily_BE.dto.pets.PetRequestDto;
import kwh.Petmily_BE.dto.pets.PetResponseDto;
import kwh.Petmily_BE.dto.pets.PetUpdateRequestDto;
import kwh.Petmily_BE.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
@Tag(name = "Pet API", description = "반려동물 관련 API")
public class PetController {

    private final PetService petService;

    @Operation(summary = "반려동물 등록", description = "새로운 반려동물을 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<PetResponseDto> registerPet(@Valid@RequestBody PetRequestDto requestDto) {
        PetResponseDto responseDto = petService.registerPet(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "반려동물 목록 조회", description = "반려동물 목록을 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<PetResponseDto>> getMyPets() {
        List<PetResponseDto> pets = petService.getMyPets();

        return ResponseEntity.ok(pets);
    }

    @Operation(summary = "특정 반려동물 조회", description = "해당 반려동물 조회합니다.")
    @GetMapping("/info/{id}")
    public ResponseEntity<PetResponseDto> getPetInfo(@PathVariable("id") Long id) {
        PetResponseDto responseDto = petService.getPetById(id);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "반려동물 정보 변경", description = "해당 반려동물 정보를 변경합니다.")
    @PatchMapping("/update/{id}")
    public ResponseEntity<PetResponseDto> updatePet(@PathVariable("id") Long id, @Valid@RequestBody PetUpdateRequestDto requestDto) {
        PetResponseDto responseDto = petService.updatePet(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "반려동물 정보 삭제", description = "해당 반려동물 정보를 삭제합니다.")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable("id") Long id) {
        petService.deletePet(id);

        return ResponseEntity.noContent().build();
    }

}
