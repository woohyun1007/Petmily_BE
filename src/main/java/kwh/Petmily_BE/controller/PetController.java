package kwh.Petmily_BE.controller;

import jakarta.validation.Valid;
import kwh.Petmily_BE.dto.PetRequestDto;
import kwh.Petmily_BE.dto.PetResponseDto;
import kwh.Petmily_BE.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping("/register")
    public ResponseEntity<PetResponseDto> register(@Valid@RequestBody PetRequestDto requestDto) {
        PetResponseDto responseDto = petService.register(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}
