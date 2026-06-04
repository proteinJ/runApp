package com.running.runapp.domain.groupRunning.controller;

import com.running.runapp.domain.groupRunning.dto.GroupRequest;
import com.running.runapp.domain.groupRunning.dto.GroupResponse;
import com.running.runapp.domain.groupRunning.service.GroupService;
import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "GroupRunning", description = "그룹 러닝 모집 및 참여")
@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupRunController {

    private final GroupService groupService;

    // ===================== Host =====================

    @Operation(summary = "그룹 러닝 생성", description = "그룹 러닝 모집글을 생성합니다. 생성자는 자동으로 방장(HOST)이 됩니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "title": "한강 저녁 러닝 같이 달려요!",
                              "content": "페이스 6분대 환영, 반포대교 집합입니다.",
                              "maxParticipants": 4,
                              "startTime": "2026-06-10T19:00:00",
                              "endTime": "2026-06-10T20:00:00",
                              "distance": 5,
                              "location": "반포 한강공원",
                              "address": "서울특별시 서초구 반포동 1"
                            }
                            """)
            )
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> groupAdd(
            @RequestBody GroupRequest.groupAdd dto,
            @LoginMember Member member
    ) {
        Long groupId = groupService.groupAdd(dto, member);
        return ResponseEntity.ok(ApiResponse.success("그룹런 생성 완료", groupId));
    }

    @Operation(summary = "그룹 러닝 수정", description = "방장만 수정 가능합니다. 변경할 필드만 전달하면 되고, null로 전달된 필드는 기존 값을 유지합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            summary = "제목과 최대 인원만 변경",
                            value = """
                            {
                              "title": "반포 한강 러닝 모집",
                              "maxParticipants": 3
                            }
                            """
                    )
            )
    )
    @PatchMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Long>> groupEdit(
            @RequestBody GroupRequest.UpdateExtraRequest dto,
            @Parameter(description = "수정할 그룹 ID", example = "1") @PathVariable Long groupId,
            @LoginMember Member member
    ) {
        Long returnGroupId = groupService.groupEdit(dto, groupId, member);
        return ResponseEntity.ok(ApiResponse.success("그룹런 수정 완료", returnGroupId));
    }

    @Operation(summary = "그룹 러닝 삭제", description = "방장만 삭제 가능합니다. 소프트 딜리트 처리됩니다.")
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<String>> groupDelete(
            @Parameter(description = "삭제할 그룹 ID", example = "1") @PathVariable Long groupId,
            @LoginMember Member member
    ) {
        groupService.groupDelete(groupId, member);
        return ResponseEntity.ok(ApiResponse.success("그룹런 삭제 완료"));
    }

    @Operation(
            summary = "그룹 러닝 시작",
            description = """
                    방장이 러닝을 시작합니다.
                    - 시작 시간이 지난 경우에만 호출 가능합니다.
                    - 이미 종료된 그룹은 시작할 수 없습니다.
                    """
    )
    @PostMapping("/{groupId}/run/start")
    public ResponseEntity<ApiResponse<GroupResponse.GroupDetail>> runStart(
            @Parameter(description = "시작할 그룹 ID", example = "1") @PathVariable Long groupId,
            @LoginMember Member member
    ) {
        GroupResponse.GroupDetail res = groupService.groupRunStart(groupId, member);
        return ResponseEntity.ok(ApiResponse.success("그룹 러닝 시작", res));
    }

    @Operation(
            summary = "그룹 러닝 종료",
            description = """
                    방장이 러닝을 종료합니다.
                    - RUNNING 상태인 그룹만 종료할 수 있습니다.
                    - 종료 후 웹소켓 종료 이벤트 발행 예정.
                    """
    )
    @PostMapping("/{groupId}/run/finish")
    public ResponseEntity<ApiResponse<GroupResponse.GroupDetail>> runFinish(
            @Parameter(description = "종료할 그룹 ID", example = "1") @PathVariable Long groupId,
            @LoginMember Member member
    ) {
        GroupResponse.GroupDetail res = groupService.groupRunFinish(groupId, member);
        return ResponseEntity.ok(ApiResponse.success("그룹 러닝 종료", res));
    }

    // ===================== Participants =====================

    @Operation(summary = "그룹 러닝 참여", description = "모집 중인 그룹에 참여합니다. 같은 시간대에 이미 참여 중인 그룹이 있으면 거부됩니다.")
    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiResponse<Long>> groupJoin(
            @Parameter(description = "참여할 그룹 ID", example = "1") @PathVariable Long groupId,
            @LoginMember Member member
    ) {
        groupService.groupJoin(groupId, member);
        return ResponseEntity.ok(ApiResponse.success("GroupID: ${groupId} 참가 완료", groupId));
    }

    @Operation(summary = "그룹 러닝 나가기", description = "참여 중인 그룹에서 나갑니다. 러닝이 시작된 이후에는 나갈 수 없습니다.")
    @DeleteMapping("/{groupId}/members/me")
    public ResponseEntity<ApiResponse<String>> groupLeave(
            @Parameter(description = "나갈 그룹 ID", example = "1") @PathVariable Long groupId,
            @LoginMember Member member
    ) {
        groupService.groupLeave(groupId, member);
        return ResponseEntity.ok(ApiResponse.success("그룹 나가기"));
    }

    // ===================== 조회 =====================

    @Operation(
            summary = "그룹 러닝 목록 조회",
            description = """
                    모집 중인 그룹 러닝 목록을 페이지네이션으로 조회합니다.
                    - 기본 정렬: 시작 시간 내림차순
                    - `isParticipating`: 내가 이미 참여 중인 그룹인지 여부
                    """
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Slice<GroupResponse.GroupSummary>>> groupList(
            @PageableDefault(size = 5, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable,
            @LoginMember Member member
    ) {
        Slice<GroupResponse.GroupSummary> groups = groupService.findAllGroups(pageable, member.getId());
        return ResponseEntity.ok(ApiResponse.success("그룹 목록 조회 완료", groups));
    }

    @Operation(
            summary = "그룹 러닝 상세 조회",
            description = """
                    그룹 러닝 상세 정보를 조회합니다.
                    - `isParticipating`: 내가 이 그룹에 참여 중인지 여부
                    - `currentParticipants` / `maxParticipants`: 현재 인원 / 최대 인원
                    - `location`: 장소명, `address`: 상세 주소
                    """
    )
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse.GroupDetail>> groupDetailInfo(
            @LoginMember Member me,
            @Parameter(description = "조회할 그룹 ID", example = "1") @PathVariable Long groupId
    ) {
        GroupResponse.GroupDetail groupDetail = groupService.getGroupDetailInfo(groupId, me.getId());
        return ResponseEntity.ok(ApiResponse.success("그룹 상세 정보 조회 완료", groupDetail));
    }
}
