package kwh.Petmily_BE.service;

import kwh.Petmily_BE.dto.CustomUserDetails;
import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + loginId));

        // Optional 처리가 위에서 끝났으므로, 바로 User 객체를 전달합니다.
        return new CustomUserDetails(user);
    }
}
