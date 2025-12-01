package kwh.Petmily_BE.repository;

import kwh.Petmily_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{

    Boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);
}
