package com.running.runapp.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 시큐리티 컨텍스트에 담길 커스텀 유저 객체
 * 기본 User 객체는 Long 타입 ID를 가질 수 없어서 상속받아 확장함
 */
@Getter
public class PrincipalDetails extends User {

    private final Long memberId;

    public PrincipalDetails(Long memberId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        // 부모인 User 클래스의 생성자 호출 (username, password, authorities는 필수)
        super(username, password, authorities);
        this.memberId = memberId;
    }
}