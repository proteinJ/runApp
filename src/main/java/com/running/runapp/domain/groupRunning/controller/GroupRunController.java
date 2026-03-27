package com.running.runapp.domain.groupRunning.controller;

import com.running.runapp.domain.groupRunning.dto.GroupRequest;
import com.running.runapp.domain.groupRunning.dto.GroupResponse;
import com.running.runapp.domain.groupRunning.service.GroupService;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
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
            @PathVariable("groupId") Long groupId,
            @LoginMember Member member
            ) {
         groupService.groupEdit(dto, groupId, member);

        return ResponseEntity.ok(ApiResponse.success("그룹런 수정 완료", groupId));
    }

    // 파티 삭제
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<String>> groupDelete(
            @PathVariable("groupId") Long groupId,
            @LoginMember Member member
    ) {
        groupService.groupDelete(groupId, member);

        return ResponseEntity.ok(ApiResponse.success("그룹런 삭제 완료"));
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
    @GetMapping
    public ResponseEntity<ApiResponse<Slice<GroupResponse.GroupSummary>>> groupList(
            @PageableDefault(size = 5, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        Slice<GroupResponse.GroupSummary> groups = groupService.findAllGroups(pageable);

        return ResponseEntity.ok(ApiResponse.success("그룹 목록 조회 완료", groups));
    }
}
