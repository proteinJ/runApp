package com.running.runapp.domain.myroom.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.myroom.dto.MyRoomRequest;
import com.running.runapp.domain.myroom.dto.MyRoomResponse;
import com.running.runapp.domain.myroom.service.MyRoomService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MyRoom", description = "마이룸(코어 컬러 · 칭호 목록)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/myroom")
public class MyRoomController {

    private final MyRoomService myRoomService;

    @Operation(
            summary = "마이룸 조회",
            description = """
                    로그인한 유저의 마이룸 정보를 조회합니다.
                    - `equippedTitle`: 현재 장착 중인 칭호명 (없으면 null)
                    - `colors`: 전체 코어 컬러 팔레트 + 구매(보유) 여부
                    - `titles`: 전체 칭호 목록 + 획득 여부 (`owned: true`면 장착 가능)
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "마이룸 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "message": "마이룸 조회 성공",
                                      "data": {
                                        "memberId": 1,
                                        "nickname": "달리기왕",
                                        "equippedTitle": "새싹 러너",
                                        "currentColorCode": "CORE_ORANGE",
                                        "colors": [
                                          { "code": "CORE_RED",    "name": "빨간색 코어", "hex": "#E53935", "owned": false },
                                          { "code": "CORE_ORANGE", "name": "주황색 코어", "hex": "#F57C00", "owned": true  },
                                          { "code": "CORE_BLACK",  "name": "검은색 코어", "hex": "#212121", "owned": false }
                                        ],
                                        "titles": [
                                          { "titleId": 1, "titleCode": "TITLE_001_START", "name": "새싹 러너",  "rarity": "NORMAL", "description": "새로운 출발과 함께 러닝이라는 재미를 느껴볼까?", "owned": true  },
                                          { "titleId": 2, "titleCode": "TITLE_002",       "name": "런린이",     "rarity": "NORMAL", "description": "첫 번째 러닝을 완료했어요!",                   "owned": true  },
                                          { "titleId": 3, "titleCode": "TITLE_003",       "name": "스피드 킹",  "rarity": "RARE",   "description": "5km를 평균 페이스 4분대로 완주!",               "owned": false }
                                        ]
                                      }
                                    }
                                    """)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<MyRoomResponse.Result>> getMyRoom(
            @LoginMember Member me,
            @ModelAttribute MyRoomRequest.Query query
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("마이룸 조회 성공", myRoomService.getMyRoom(me))
        );
    }

    @Operation(
            summary = "코어 컬러 변경",
            description = "구매·보유한 코어 컬러로 변경합니다. 보유하지 않은 컬러로 변경 시 403을 반환합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "컬러 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "message": "코어 컬러 변경 성공",
                                      "data": { "currentColorCode": "CORE_ORANGE" }
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "보유하지 않은 컬러",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": "A005",
                                      "message": " 접근 권한이 없습니다."
                                    }
                                    """)
                    )
            )
    })
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
