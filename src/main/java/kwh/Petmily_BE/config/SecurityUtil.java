package kwh.Petmily_BE.config;

import kwh.Petmily_BE.dto.users.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.*;

@Component
public class SecurityUtil {
    // 현재 Security Context에 저장된 인증된 사용자의 ID를 반환한다.
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("인증된 사용자 정보가 Security Context에 없습니다. (로그인 필요)");
        }

        // Principal (주체) 객체 추출
        final Object principal = authentication.getPrincipal();

        // CustomUserDetails 타입 확인 및 ID 반환
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) principal;

            // CustomUserDetails 내부의 User 엔티티에서 ID를 가져와 반환
            return customUserDetails.getUser().getId();
        } else if (principal.equals("anonymousUser")) {
            // permitAll() 등으로 익명 접근이 허용된 경우 (토큰이 없는 경우)
            throw new AccessDeniedException("익명 사용자는 이 작업을 수행할 수 없습니다.");
        }

        // 예상치 못한 Principal 타입인 경우
        throw new AccessDeniedException("유효하지 않은 사용자 정보 형식입니다.");
    }
}
