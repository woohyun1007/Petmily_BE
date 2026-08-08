package kwh.Petmily_BE.domain.auth.service;

import kwh.Petmily_BE.domain.auth.entity.EmailVerification;
import kwh.Petmily_BE.domain.auth.repository.EmailVerificationRepository;
import kwh.Petmily_BE.domain.user.repository.UserRepository;
import kwh.Petmily_BE.global.error.ErrorCode;
import kwh.Petmily_BE.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private static final Duration CODE_TTL = Duration.ofMinutes(10);

    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    @Transactional
    public void sendVerificationCode(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        String code = generateCode();
        LocalDateTime expiresAt = LocalDateTime.now().plus(CODE_TTL);

        EmailVerification verification = emailVerificationRepository.findByEmail(email)
                .map(existing -> {
                    existing.issue(code, expiresAt);
                    return existing;
                })
                .orElseGet(() -> new EmailVerification(email, code, expiresAt));

        emailVerificationRepository.save(verification);
        sendMail(email, code);
    }

    @Transactional
    public void verifyCode(String email, String code) {
        EmailVerification verification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_VERIFICATION_FAILED));

        LocalDateTime now = LocalDateTime.now();
        if (verification.isExpired(now) || verification.isVerified() || !code.equals(verification.getCode())) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_FAILED);
        }

        verification.verify(now);
        emailVerificationRepository.save(verification);
    }

    @Transactional(readOnly = true)
    public void requireVerifiedEmail(String email) {
        if (!emailVerificationRepository.existsByEmailAndVerifiedAtIsNotNull(email)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_REQUIRED);
        }
    }

    @Transactional
    public void consumeVerifiedEmail(String email) {
        emailVerificationRepository.deleteByEmail(email);
    }

    private void sendMail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (mailFrom != null && !mailFrom.isBlank()) {
            message.setFrom(mailFrom);
        }
        message.setTo(email);
        message.setSubject("[Petmily] 이메일 인증번호");
        message.setText("인증번호는 [" + code + "] 입니다. 10분 이내에 입력해주세요.");
        mailSender.send(message);
    }

    private String generateCode() {
        SecureRandom secureRandom = new SecureRandom();
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }
}
