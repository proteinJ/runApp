package com.running.runapp.domain.member.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.member.dto.SocialResponse;
import com.running.runapp.domain.member.service.FeedService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FeedController {

    private final FeedService feedService;

    // 친구 피드 조회
    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<List<SocialResponse.FeedItem>>> feed(
            @LoginMember Member me
    ) {
        List<SocialResponse.FeedItem> res = feedService.friendFeed(me);
        return ResponseEntity.ok(ApiResponse.success("친구 피드 조회 완료", res));
    }
}