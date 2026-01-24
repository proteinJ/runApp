package com.running.runapp.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", " 올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", " 잘못된 HTTP 메서드 호출입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", " 서버 내부 오류가 발생했습니다."),

    // Member (회원 관련)
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "M001", " 이미 존재하는 이메일입니다."),
    NICKNAME_DUPLICATION(HttpStatus.BAD_REQUEST, "M002", "이미 존재하는 닉네임입니다."),
    LOGIN_INPUT_INVALID(HttpStatus.BAD_REQUEST, "M003", " 이메일 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M004", " 존재하지 않는 회원입니다."),

    // Auth (인증 관련)
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A001", " 인증에 실패하였습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A002", " 토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", " 유효하지 않은 토큰입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A004", " 접근 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
