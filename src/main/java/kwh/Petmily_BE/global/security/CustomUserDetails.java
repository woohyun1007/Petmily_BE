package kwh.Petmily_BE.global.security;

import kwh.Petmily_BE.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String loginId;
    private final String password;

    // 로그인 용
    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.password = user.getPassword();
    }

    // 토큰 정보용
    public CustomUserDetails(Long id, String loginId) {
        this.id = id;
        this.loginId = loginId;
        this.password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() { return loginId; }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }

}
