package kwh.Petmily_BE.domain.user.repository;

import kwh.Petmily_BE.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);
}

