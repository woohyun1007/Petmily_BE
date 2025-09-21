package kwh.Petmily_BE.service;

import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User signup(User user) {
        return userRepository.save(user);
    }
}
