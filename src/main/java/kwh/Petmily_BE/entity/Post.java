package kwh.Petmily_BE.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Post {
    private String loginId;
    private String password;
    private String email;
    private String name;
    private LocalDate birthDate;
}
