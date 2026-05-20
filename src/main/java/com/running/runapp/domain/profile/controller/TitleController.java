package com.running.runapp.domain.profile.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.profile.dto.TitleResponse;
import com.running.runapp.domain.profile.service.TitleService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Title", description = "칭호 관련 API")
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

}