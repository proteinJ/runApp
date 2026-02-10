package com.running.runapp.domain.running.service;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.repository.MemberRepository;
import com.running.runapp.domain.running.domain.RunStatus;
import com.running.runapp.domain.running.domain.RunningRecord;
import com.running.runapp.domain.running.dto.RunResponse;
import com.running.runapp.domain.running.dto.RunRequest;
import com.running.runapp.domain.running.repository.RunningRecordRepository;
import com.running.runapp.domain.running.util.DistanceUtils;
import com.running.runapp.domain.running.util.GeometryUtils;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.LineString;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RunService {

    private final RunningRecordRepository runningRecordRepository;
    private final MemberRepository memberRepository;

    /**
     * Run 시작
     */
    public RunResponse.RunStartResponse start(RunRequest.RunStartRequest request) {
        Member member = getCurrentMember();

        RunningRecord record = RunningRecord.create(member, request.getStartTime());
        runningRecordRepository.save(record);

        log.info("Run start: memberId={}, runId={}", member.getId(), record.getId());

        return RunResponse.RunStartResponse.builder()
                .runId(record.getId())
                .memberId(member.getId())
                .build();
    }

    /**
     * Run 종료
     */
    public RunResponse.RunFinishResponse finish(Long runId, RunRequest.RunFinishRequest request) {
        Member member = getCurrentMember();

        RunningRecord record = runningRecordRepository.findByIdAndMember_Id(runId, member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

        if (record.getStatus().equals(RunStatus.FINISHED)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        LineString lineString = GeometryUtils.toLineString(request.getPath());
        double calculatedDistance = DistanceUtils.totalDistanceMeter(request);
        record.finish(request.getEndTime(), calculatedDistance, lineString);

        log.info("Run finish: memberId={}, runId={}, totalDistance={}",
                member.getId(), record.getId(), calculatedDistance);

        return RunResponse.RunFinishResponse.builder()
                .runId(record.getId())
                .totalDistance(record.getTotalDistance())
                .earnedPoints(0)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RunResponse.MyRunSummaryResponse> myRuns() {
        Member member = getCurrentMember();

        return runningRecordRepository.findByMember_IdOrderByStartTimeDesc(member.getId())
                .stream()
                .map(RunResponse.MyRunSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RunResponse.RunDetailResponse detail(Long runId) {
        Member member = getCurrentMember();

        RunningRecord record = runningRecordRepository.findByIdAndMember_Id(runId, member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

        return RunResponse.RunDetailResponse.from(record);
    }

    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}