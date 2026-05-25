package com.running.runapp.domain.shop.controller;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.domain.shop.dto.ShopRequest;
import com.running.runapp.domain.shop.dto.ShopResponse;
import com.running.runapp.domain.shop.service.ShopService;
import com.running.runapp.global.common.ApiResponse;
import com.running.runapp.global.common.annotaion.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Shop", description = "포인트 상점")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop")
public class ShopController {

    private final ShopService shopService;

    @Operation(summary = "상품 목록 조회", description = "상점 상품 목록을 조회합니다.")
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<ShopResponse.ItemList>> items(
            @Parameter(description = "판매중인 상품만 조회", example = "true")
            @ModelAttribute ShopRequest.ItemListQuery query
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("상품 목록 조회 성공", shopService.listItems(query))
        );
    }

    @Operation(summary = "상품 구매", description = "포인트를 차감하고 구매 내역을 저장합니다.")
    @PostMapping("/purchases")
    public ResponseEntity<ApiResponse<ShopResponse.PurchaseResult>> purchase(
            @LoginMember Member me,
            @RequestBody @Valid ShopRequest.Purchase request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("상품 구매 성공", shopService.purchase(me, request))
        );
    }
}