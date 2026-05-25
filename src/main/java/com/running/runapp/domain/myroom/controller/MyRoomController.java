package com.running.runapp.domain.myroom.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.myroom.dto.MyRoomRequest;
import com.running.runapp.domain.myroom.dto.MyRoomResponse;
import com.running.runapp.domain.myroom.service.MyRoomService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MyRoom", description = "마이룸(코어 컬러)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/myroom")
public class MyRoomController {

    private final MyRoomService myRoomService;

    @Operation(summary = "마이룸 조회", description = "닉네임/장착칭호/현재 코어컬러/팔레트를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<MyRoomResponse.Result>> getMyRoom(
            @LoginMember Member me,
            @ModelAttribute MyRoomRequest.Query query
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("마이룸 조회 성공", myRoomService.getMyRoom(me))
        );
    }

    @Operation(summary = "코어 컬러 변경", description = "구매/보유한 코어 컬러로 변경합니다.")
    @PatchMapping("/core-color")
    public ResponseEntity<ApiResponse<MyRoomResponse.ChangeCoreColorResult>> changeCoreColor(
            @LoginMember Member me,
            @RequestBody @Valid MyRoomRequest.ChangeCoreColor request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("코어 컬러 변경 성공", myRoomService.changeCoreColor(me, request))
        );
    }
}