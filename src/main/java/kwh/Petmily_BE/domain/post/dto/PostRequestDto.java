package kwh.Petmily_BE.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.post.entity.Post;
import kwh.Petmily_BE.domain.post.entity.enums.PostCategory;
import kwh.Petmily_BE.domain.post.entity.enums.PriceUnit;
import kwh.Petmily_BE.domain.post.entity.enums.RequestStatus;
import kwh.Petmily_BE.domain.user.entity.User;

public record PostRequestDto(
        @NotBlank(message = "제목은 필수입니다.")
        String title,           // 제목

        @NotBlank(message = "내용은 필수입니다.")
        String content,         // 내용

        String region,          // 지역, 주소 등

        PriceUnit priceUnit,

        Long price,              // 의뢰비

        @NotNull(message = "카테고리를 선택해주세요.")
        PostCategory category,

        @NotNull
        RequestStatus status,

        String petName,
        Long petId,

        // 카카오맵에서 받은 위경도
        Double latitude,
        Double longitude
) {
        // DTO -> Entity 변환 시 빌더 사용
        public Post toEntity(User writer, Pet pet) {
                return Post.builder()
                        .title(title)
                        .content(content)
                        .region(region)
                        .priceUnit(priceUnit)
                        .price(price)
                        .category(category)
                        .status(status)
                        .latitude(latitude)
                        .longitude(longitude)
                        .writer(writer)
                        .pet(pet)
                        .build();
        }
}
