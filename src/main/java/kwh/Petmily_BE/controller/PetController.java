package kwh.Petmily_BE.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kwh.Petmily_BE.dto.pets.PetRequestDto;
import kwh.Petmily_BE.dto.pets.PetResponseDto;
import kwh.Petmily_BE.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
@Tag(name = "Pet API", description = "반려동물 관련 API")
public class PetController {

    private final PetService petService;

    @Operation(summary = "반려동물 등록", description = "새로운 반려동물을 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<PetResponseDto> register(@Valid@RequestBody PetRequestDto requestDto) {
        PetResponseDto responseDto = petService.registerPet(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}
