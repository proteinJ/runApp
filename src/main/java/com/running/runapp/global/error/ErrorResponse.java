package com.running.runapp.global.error;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Error 응답용 DTO
 */

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 생성자를 숨겨서 정적 메서드 사용을 강제함
public class ErrorResponse {

    private final String status;

    private final String code;

    private final String message;

    public ErrorResponse(ErrorCode errorCode, String message) {
        this.status = "fail";
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 이렇게 하면 외부에서는 new ErrorResponse(...)를 못 쓰고,
     * 무조건 ErrorResponse.of(ErrorCode.EMAIL_DUPLICATION)과 같이
     * 의미가 명확한 방식으로만 객체를 만들 수 있게 됩니다.
     * @param errorCode
     * [ 정적 팩토리 메서드 ]
     */

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse("fail", errorCode.getCode(), errorCode.getMessage());
    }
}
