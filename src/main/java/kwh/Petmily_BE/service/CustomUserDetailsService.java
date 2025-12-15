package kwh.Petmily_BE.service;

import kwh.Petmily_BE.dto.users.CustomUserDetails;
import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // JWT에선 LoginId를 사용하므로 Username이 아닌 LoginId를 기반으로 조회해야함
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + loginId));

        return new CustomUserDetails(user);
    }
}
