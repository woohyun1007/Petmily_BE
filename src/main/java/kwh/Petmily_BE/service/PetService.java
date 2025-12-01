package kwh.Petmily_BE.service;

import jakarta.transaction.Transactional;
import kwh.Petmily_BE.dto.JoinResponseDto;
import kwh.Petmily_BE.dto.PetRequestDto;
import kwh.Petmily_BE.dto.PetResponseDto;
import kwh.Petmily_BE.entity.Pet;
import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    public PetResponseDto register(PetRequestDto requestDto) {

        //RequestDto -> Pet Entity 변환
        Pet newPet = Pet.builder()
                .name(requestDto.name())
                .type(requestDto.type())
                .detail_type(requestDto.detail_type())
                .age(requestDto.age())
                .image(requestDto.image())
                .genders(requestDto.genders())
                .caution(requestDto.caution())
                .owner(requestDto.owner())
                .build();

        //DB에 저장
        Pet savedPet = petRepository.save(newPet);

        //Entity -> ReponseDto 변환 후 반환
        return new PetResponseDto(savedPet);
    }
}
