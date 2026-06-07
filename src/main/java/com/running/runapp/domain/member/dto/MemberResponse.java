package com.running.runapp.domain.member.dto;

import com.running.runapp.domain.member.domain.Member;

public class MemberResponse {

    public record Me(
            Long id,
            String email,
            String nickname,
            String realname
    ) {
        public static Me from(Member member) {
            return new Me(
                    member.getId(),
                    member.getEmail(),
                    member.getProfile().getNickname(),
                    member.getRealname()
            );
        }
    }
}
