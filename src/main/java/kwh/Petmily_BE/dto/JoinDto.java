package kwh.Petmily_BE.dto;


import kwh.Petmily_BE.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
public class JoinDto {
    private String username;
    private String password;
    private String email;
    private String loginId;
    private Set<User.Role> role;
}
