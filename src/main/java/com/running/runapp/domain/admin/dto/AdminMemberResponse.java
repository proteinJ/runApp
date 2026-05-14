package com.running.runapp.domain.admin.dto;

import com.running.runapp.domain.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminMemberResponse {

    @Getter
    @Builder
    public static class Summary {
        private Long memberId;
        private String email;
        private String nickname;
        private String role;
        private Integer totalPoint;

        public static Summary from(Member m) {
            return Summary.builder()
                    .memberId(m.getId())
                    .email(m.getEmail())
                    .role(m.getRole().name())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Detail {
        private Long memberId;
        private String email;
        private String nickname;
        private String realname;
        private String city;
        private String street;
        private String imageUrl;
        private String role;
        private Integer totalPoint;

        public static Detail from(Member m) {
            return Detail.builder()
                    .memberId(m.getId())
                    .email(m.getEmail())
                    .realname(m.getRealname())
                    .city(m.getCity())
                    .street(m.getStreet())
                    .imageUrl(m.getImageUrl())
                    .role(m.getRole().name())
                    .build();
        }
    }
}