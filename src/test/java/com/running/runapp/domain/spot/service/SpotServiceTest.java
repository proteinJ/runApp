package com.running.runapp.domain.spot.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.domain.point.PointHistoryRepository;
import com.running.runapp.domain.running.RunStatus;
import com.running.runapp.domain.running.repository.RunningRecordRepository;
import com.running.runapp.domain.spot.domain.Spot;
import com.running.runapp.domain.spot.dto.SpotRequest;
import com.running.runapp.domain.spot.dto.SpotResponse;
import com.running.runapp.domain.spot.repository.SpotRepository;
import com.running.runapp.domain.spot.repository.SpotVisitLogRepository;
import com.running.runapp.global.common.LocationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SpotServiceTest {

    @InjectMocks SpotService spotService;
    @Mock RunningRecordRepository runningRecordRepository;
    @Mock MemberRepository memberRepository;
    @Mock SpotRepository spotRepository;
    @Mock PointHistoryRepository pointHistoryRepository;
    @Mock SpotVisitLogRepository spotVisitLogRepository;

    @Test
    public void 체크인_성공_테스트() throws Exception {
        //given
        // 1. 테스트용 데이터 준비
        Long spotId = 1L;
        Long runId = 100L; // ID 통일
        String userEmail = "test@email.com";

        Member fakeMember = Member.builder().id(1L).email(userEmail).totalPoint(0).build();

        // Spot에 Location(Point) 정보가 있어야 거리 계산 에러가 안 납니다.
        // (거리 계산 로직이 있다면 Point 객체도 Mocking하거나 설정 필요)
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(129.0931, 35.2452)); // 경도, 위도 순서 주의!

        Spot fakeSpot = Spot.builder()
                .id(spotId)
                .name("이마트 금정점")
                .location(point)
                .rewardAmount(100)
                .latitude(35.2452)
                .longitude(129.0931)
                .build();

        RunningRecord fakeRun = RunningRecord.builder()
                .id(runId)
                .member(fakeMember)
                .runStatus(RunStatus.RUNNING)
                .build();

        // 2. Mock 행동 정의 (Stubbing) - 실제 서비스 코드와 똑같이 맞춰야 함!

        // "이메일로 찾으면 fakeMember 줘!"
        given(memberRepository.findByEmail(userEmail)).willReturn(Optional.of(fakeMember));

        // "runId(100)으로 찾으면 fakeRun 줘!"
        given(runningRecordRepository.findById(runId)).willReturn(Optional.of(fakeRun));

        // "spotId(1)로 찾으면 fakeSpot 줘!"
        given(spotRepository.findById(spotId)).willReturn(Optional.of(fakeSpot));


        SpotRequest.SpotCheckinRequest requestDto = new SpotRequest.SpotCheckinRequest(
                runId, // ⚠️ 위에서 정의한 100L과 일치해야 함
                35.2451, // Spot 위치와 매우 가깝게 설정 (거리 계산 통과 위해)
                129.0934,
                LocalDateTime.now()
        );

        System.out.println("스팟 위도: " + fakeSpot.getLocation().getY());
        System.out.println("스팟 경도: " + fakeSpot.getLocation().getX());

        // when
        SpotResponse.SpotCheckinResponse response = spotService.spotCheckin(spotId, requestDto, userEmail);

        // then
        assertThat(response).isNotNull();
        assertThat(response.spotName()).isEqualTo("이마트 금정점");
    }

    // 메서드 접근 제어자 private일 경우 삭제하고 테스트
    @Test
    public void 하버사인을_이용한_거리계산_테스트() throws Exception {
        //given
        double myLat = 37.1234;
        double spotLat = 37.5678;

        //when
        double result = LocationUtils.calculateDistance(myLat, 127.0, spotLat, 127.0);

        //then
        assertThat(result).isGreaterThan(30.0);
    }
    
    @Test
    public void 주변스팟_조회_테스트() throws Exception {
        //given
        
        //when
        
        //then
    }
}