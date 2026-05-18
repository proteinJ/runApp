package com.running.runapp.domain.profile.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.domain.Role;
import com.running.runapp.domain.profile.dto.TitleRequest;
import com.running.runapp.domain.profile.dto.TitleResponse;
import com.running.runapp.domain.profile.service.TitleService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/title")
public class TitleController {

    private final TitleService titleService;

    @Operation(summary = "전체 칭호 목록 조회(도감용)")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TitleResponse.TitleInfo>>> getAllTitle(@LoginMember Member me) {
         List<TitleResponse.TitleInfo> titleList = titleService.getAllTitle(me);
    
        return ResponseEntity.ok(ApiResponse.success("전체 칭호 목록 조회 성공", titleList));
    }

    /**
     * Admin
     */
    @Operation(summary = "칭호 추가", description = "관리자용 칭호 추가")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<TitleResponse.TitleInfo>> addNewTitle(
            @LoginMember Member me,
            @RequestBody @Valid TitleRequest.addNewTitle dto
            ) {
        if (me.getRole() != Role.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        TitleResponse.TitleInfo title = titleService.addNewTitle(dto);

        return ResponseEntity.ok(ApiResponse.success("새로운 칭호 추가 성공", title));
    }
}
