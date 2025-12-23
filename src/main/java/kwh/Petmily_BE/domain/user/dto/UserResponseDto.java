package kwh.Petmily_BE.domain.user.dto;

import kwh.Petmily_BE.domain.user.entity.enums.Role;
import kwh.Petmily_BE.domain.user.entity.User;

import java.util.Set;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        String loginId,
        String password,
        Set<Role> roles
) {
    public UserResponseDto(User user) {
        this(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getLoginId(),
                user.getPassword(),
                user.getRoles()
        );
    }
}
