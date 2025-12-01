package kwh.Petmily_BE.entity;

import jakarta.persistence.*;
import kwh.Petmily_BE.enums.RequestStatus;

import java.util.List;

public class CareRequest {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String region;
    private int price;

    @ElementCollection(fetch = FetchType.EAGER) // EAGER: 요청 조회 시 status도 항상 같이 가져옴
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pet pet;
}
