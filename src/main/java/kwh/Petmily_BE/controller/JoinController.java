package kwh.Petmily_BE.controller;

import jakarta.validation.Valid;
import kwh.Petmily_BE.dto.JoinRequestDto;
import kwh.Petmily_BE.dto.JoinResponseDto;
import kwh.Petmily_BE.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/join")
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/signup")
    public ResponseEntity<JoinResponseDto> signUp(@Valid @RequestBody JoinRequestDto requestDto) {
        JoinResponseDto responseDto = joinService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
