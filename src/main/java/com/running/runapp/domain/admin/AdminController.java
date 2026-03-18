package com.running.runapp.domain.admin;

import com.running.runapp.domain.member.domain.Member;
import com.running.runapp.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/v1/admin")
public class AdminController {


//    @GetMapping("/member")
//    public ResponseEntity<?> getAllMember() {
//        List<Member> memberList = AdminService.getAllMember();
//
//        return ResponseEntity.ok(ApiResponse("회원 목록 불러오기", memberList)).build();
//    }
}
