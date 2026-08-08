package kwh.Petmily_BE.domain.auth.repository;

import kwh.Petmily_BE.domain.auth.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmail(String email);

    boolean existsByEmailAndVerifiedAtIsNotNull(String email);

    void deleteByEmail(String email);
}
