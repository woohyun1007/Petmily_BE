package kwh.Petmily_BE.service;

import kwh.Petmily_BE.entity.Post;
import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public String signup(Post post) {
        Optional<User> findUser = userRepository.findByName(post.getLoginId());
        if(findUser.isPresent()) {
            return "중복된 ID 입니다.";
        }

        User user = User.builder()
                .loginId(post.getLoginId())
                .password(post.getPassword())
                .name(post.getName())
                .birthDate(post.getBirthDate())
                .email(post.getEmail())
                .build();

        userRepository.save(user);
        return "가입되었습니다.";
    }
}
