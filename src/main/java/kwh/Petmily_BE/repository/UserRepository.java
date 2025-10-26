package kwh.Petmily_BE.repository;

import kwh.Petmily_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{

    Boolean existsByUsername(String username);

    User findByUsername(String username);
}
