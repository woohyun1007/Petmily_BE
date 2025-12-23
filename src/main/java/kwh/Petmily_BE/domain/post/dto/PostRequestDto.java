package kwh.Petmily_BE.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.post.entity.Post;
import kwh.Petmily_BE.domain.post.entity.enums.PostCategory;
import kwh.Petmily_BE.domain.post.entity.enums.RequestStatus;
import kwh.Petmily_BE.domain.user.entity.User;

public record PostRequestDto(
        @NotBlank(message = "제목은 필수입니다.")
        String title,           // 제목

        @NotBlank(message = "내용은 필수입니다.")
        String content,         // 내용

        @NotBlank(message = "지역 정보는 필수입니다.")
        String region,          // 지역, 주소 등

        int price,              // 의뢰비

        @NotNull(message = "카테고리를 선택해주세요.")
        PostCategory category,

        @NotNull(message = "상태값은 필수입니다.")
        RequestStatus status,

        Long petId
) {
        public Post toEntity(User writer, Pet pet) {
                return Post.builder()
                        .title(this.title)
                        .content(this.content)
                        .region(this.region)
                        .price(this.price)
                        .category(this.category)
                        .status(this.status)
                        .writer(writer)
                        .pet(pet)
                        .build();
        }
}
