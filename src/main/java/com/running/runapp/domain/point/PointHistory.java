package com.running.runapp.domain.point; // 1. 주소

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.spot.domain.Spot;
import jakarta.persistence.*; // 3. DB 도구 상자 가져오기
import lombok.*;

import java.time.LocalDateTime; // 6. 날짜/시간 도구

@Entity // 7. "나는 DB 테이블이야!" 표시
@Table(name = "point_history") // 8. 연결할 DB 테이블 이름
@Getter // 9. 데이터 조회 기능 자동 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 10. 빈 껍데기 생성 기능
@Builder // 이제 @AllArgsConstructor 덕분에 잘 작동합니다!
@AllArgsConstructor // 빌더가 사용할 생성자
public class PointHistory { // 11. 여기서부터 설계도 시작

    @Id // 12. 주민등록번호(PK) 표시
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_hist_id") // 13. DB 컬럼 이름표
    private Long id; // 14. 자바에서 쓸 이름

    @ManyToOne(fetch = FetchType.LAZY) // 15. "회원 1명이 포인트 내역 여러 개 가질 수 있다" (관계 설정)
    @JoinColumn(name = "member_id") // 16. 연결고리(Foreign Key) 이름
    private Member member; // 17. 친구가 만든 Member와 연결!

    private Integer amount; // 18. 포인트 양 (숫자)

    @Enumerated(EnumType.STRING) // 19. "EARN, SPEND 같은 글자로 저장해줘"
    private PointType type; // 20. 포인트 타입 (쌓은 건지, 쓴 건지)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id")
    private Spot spot;

    private String description; // 21. 설명

    @Column(name = "created_at") // 22. DB 컬럼 이름 매칭
    private LocalDateTime createdAt; // 23. 날짜와 시간

    // 포인트 타입 종류 정의 (내부에 간단히 만듦)
    public enum PointType {
        EARN, SPEND
    }
}
