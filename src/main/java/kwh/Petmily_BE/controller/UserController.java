package kwh.Petmily_BE.controller;

import kwh.Petmily_BE.entity.Post;
import kwh.Petmily_BE.entity.User;
import kwh.Petmily_BE.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signup(@RequestBody Post user) {
        return userService.signup(user);
    }
}
