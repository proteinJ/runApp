package com.running.runapp.domain.groupRunning.controller;

import com.running.runapp.domain.groupRunning.dto.GroupRequest;
import com.running.runapp.domain.groupRunning.service.GroupService;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groupRun")
@RequiredArgsConstructor
@Slf4j
public class GroupRunController {

    private final GroupService groupService;

    /**
     * Host 입장
     */

    // 파티 생성
    @PostMapping("/group/add")
    public ResponseEntity<ApiResponse<Long>> groupAdd(
            @RequestBody GroupRequest.groupAdd dto,
            @LoginMember Member member
    ) {
        Long groupId = groupService.groupAdd(dto, member);

        return ResponseEntity.ok(ApiResponse.success("그룹런 생성 완료", groupId));
    }

    // 파티 수정
    @PostMapping("/group/edit/{groupId}")
    public ResponseEntity<ApiResponse<Long>> groupEdit(
            @RequestBody GroupRequest.UpdateExtraRequest dto,
            @PathVariable("groupId") Long groupId,
            @LoginMember Member member
            ) {
         groupService.groupEdit(dto, groupId, member);

        return ResponseEntity.ok(ApiResponse.success("그룹런 수정 완료", groupId));
    }

    // 파티 삭제
    @PostMapping("/group/delete/{groupId}")
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
}
