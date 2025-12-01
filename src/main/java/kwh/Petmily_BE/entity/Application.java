package kwh.Petmily_BE.entity;

import jakarta.persistence.*;
import kwh.Petmily_BE.enums.AppStatus;

@Entity
public class Application {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private CareRequest request; // 어떤 요청 글인지

    @ManyToOne
    private User sitter;         // 지원한 돌보미

    @Enumerated(EnumType.STRING)
    private AppStatus status;    // PENDING(대기), ACCEPTED(수락), REJECTED(거절)
}