package com.running.runapp.domain.groupRunning.controller;

import com.running.runapp.domain.groupRunning.dto.GroupRequest;
import com.running.runapp.domain.groupRunning.dto.GroupResponse;
import com.running.runapp.domain.groupRunning.service.GroupService;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupRunController {

    private final GroupService groupService;

    /**
     * Host 입장
     */

    // 파티 생성
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> groupAdd(
            @RequestBody GroupRequest.groupAdd dto,
            @LoginMember Member member
    ) {
        Long groupId = groupService.groupAdd(dto, member);

        return ResponseEntity.ok(ApiResponse.success("그룹런 생성 완료", groupId));
    }

    // 파티 수정
    @PatchMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Long>> groupEdit(
            @RequestBody GroupRequest.UpdateExtraRequest dto,
            @PathVariable Long groupId,
            @LoginMember Member member
            ) {
         Long returnGroupId = groupService.groupEdit(dto, groupId, member);

        return ResponseEntity.ok(ApiResponse.success("그룹런 수정 완료", returnGroupId));
    }

    // 그룹 나가기
    @DeleteMapping("/{groupId}/members/me")
    public ResponseEntity<ApiResponse<String>> groupLeave(
            @PathVariable Long groupId,
            @LoginMember Member member
    ) {
        groupService.groupLeave(groupId, member);

        return ResponseEntity.ok(ApiResponse.success("그룹 나가기"));
    }

    // 파티 삭제
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<String>> groupDelete(
            @PathVariable Long groupId,
            @LoginMember Member member
    ) {
        groupService.groupDelete(groupId, member);

        return ResponseEntity.ok(ApiResponse.success("그룹런 삭제 완료"));
    }

    // 그룹 러닝 시작
    @PostMapping("/{groupId}/run/start")
    public ResponseEntity<ApiResponse<GroupResponse.GroupDetail>> runStart(
            @PathVariable Long groupId,
            @LoginMember Member member
    ) {
        GroupResponse.GroupDetail res = groupService.groupRunStart(groupId, member);

        return ResponseEntity.ok(ApiResponse.success("그룹 러닝 시작", res));
    }

    // 그룹 러닝 종료
    @PostMapping("/{groupId}/run/finish")
    public ResponseEntity<ApiResponse<GroupResponse.GroupDetail>> runFinish(
            @PathVariable Long groupId,
            @LoginMember Member member
    ) {
        GroupResponse.GroupDetail res = groupService.groupRunFinish(groupId, member);

        return ResponseEntity.ok(ApiResponse.success("그룹 러닝 종료", res));
    }



    /**
     * Participants 입장
     */
    // 그룹 참여
    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiResponse<Long>> groupJoin(
            @PathVariable("groupId") Long groupId,
            @LoginMember Member member
    ) {
        groupService.groupJoin(groupId, member);

        return ResponseEntity.ok(ApiResponse.success("GroupID: ${groupId} 참가 완료", groupId));
    }

    // 그룹 목록 조회
    @Operation(summary = "그룹 러닝 목록 조회", description = "사용자들이 작성한 그룹러닝 모집글 목록을 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Slice<GroupResponse.GroupSummary>>> groupList(
            @PageableDefault(size = 5, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable,
            @LoginMember Member member
    ) {
        Slice<GroupResponse.GroupSummary> groups = groupService.findAllGroups(pageable, member.getId());

        return ResponseEntity.ok(ApiResponse.success("그룹 목록 조회 완료", groups));
    }

    // 그룹러닝 상세 페이지
    @Operation(summary = "그룹 러닝 상세 조회", description = "그룹 러닝 목록에서 하나 선택 시 보여지는 상세 정보")
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse.GroupDetail>> groupDetailInfo(
            @LoginMember Member me,
            @PathVariable Long groupId
    ) {
        GroupResponse.GroupDetail groupDetail = groupService.getGroupDetailInfo(groupId);
        return ResponseEntity.ok(ApiResponse.success("그룹 상세 정보 조회 완료", groupDetail));
    }
}
