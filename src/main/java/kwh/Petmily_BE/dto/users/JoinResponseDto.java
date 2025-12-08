package kwh.Petmily_BE.dto.users;

import kwh.Petmily_BE.enums.Role;
import kwh.Petmily_BE.entity.User;

import java.util.Set;

public record JoinResponseDto(
        Long id,
        String username,
        String email,
        String loginId,
        Set<Role> roles
) {
    public JoinResponseDto(User user) {
        this(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getLoginId(),
                user.getRoles()
        );
    }
}
