package com.running.runapp.global.init;

import com.running.runapp.domain.admin.service.AdminService;
import com.running.runapp.domain.member.dto.MemberRequest;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.domain.profile.domain.Rarity;
import com.running.runapp.domain.profile.domain.Title;
import com.running.runapp.domain.profile.repository.TitleRepository;
import com.running.runapp.domain.spot.dto.SpotRequest;
import com.running.runapp.domain.spot.repository.SpotRepository;
import com.running.runapp.domain.spot.service.SpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitalizer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final TitleRepository titleRepository;
    private final SpotRepository spotRepository;
    private final SpotService spotService;
    private final AdminService adminService;

    @Override
    public void run(ApplicationArguments args) {
        if (titleRepository.count() == 0) {
            Title title = Title.builder()
                    .name("새싹 러너")
                    .description("새로운 출발과 함께 러닝이라는 재미를 느껴볼까? 당신의 열정이 너무 뜨거워!")
                    .titleCode("TITLE_001_START")
                    .rarity(Rarity.NORMAL)
                    .expBonusRatio(0.0)
                    .pointBonusRatio(0.0)
                    .build();
            titleRepository.save(title);
            log.info("[Init] 기본 칭호 초기화 완료!");
        }


        if (memberRepository.findByEmail("admin@test.com").isEmpty()) {
            MemberRequest.Join req = new MemberRequest.Join("admin@test.com", "admin1004", "ADMIN", "ADMIN");
            adminService.join(req);

            log.info("[Init] 어드민 계정 초기화 완료!");
        }

        if (spotRepository.count() == 0) {
            SpotRequest.SpotCreateRequest req = new SpotRequest.SpotCreateRequest(
                    "구서역 (1호선)",
                    "금정구의 자랑 구서동! 체크인 시 100포인트 증정",
                    10,
                    null,
                    35.2475,
                    129.0914,
                    100L
                    );
            spotService.createSpot(req);
            log.info("[Init] 스팟 초기화 완료!");
        }
    }

}
