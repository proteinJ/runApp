package com.running.runapp.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "CM001", " 올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "CM002", " 잘못된 HTTP 메서드 호출입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CM003", " 서버 내부 오류가 발생했습니다."),

    // Member (회원 관련)
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "M001", " 이미 존재하는 이메일입니다."),
    NICKNAME_DUPLICATION(HttpStatus.BAD_REQUEST, "M002", "이미 존재하는 닉네임입니다."),
    INVALID_LOGIN_CREDENTIALS(HttpStatus.BAD_REQUEST, "M003", " 이메일 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M004", " 존재하지 않는 회원입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "M005", "비밀번호가 올바르지 않습니다."),

    // Notice (공지사항 관련)
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "공지사항을 찾을 수 없습니다."),

    // Notification (알림 관련)
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NF001", "존재하지 않는 알림입니다."),

    // Auth (인증 관련)
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A001", " 인증에 실패하였습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A002", " 토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", " 유효하지 않은 토큰입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A005", " 접근 권한이 없습니다."),

    // Spot (장소 관련)
    SPOT_NAME_DUPLICATION(HttpStatus.BAD_REQUEST, "S001", " 이미 존재하는 이름입니다."),
    POINT_DUPLICATION(HttpStatus.BAD_REQUEST, "S002", "이미 존재하는 좌표입니다."),
    SPOT_NOT_FOUND(HttpStatus.NOT_FOUND, "S004", " 존재하지 않는 Spot입니다."),

    // Checkin (체크인 관련)
    RUNNING_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "존재하지 않는 Running 기록입니다."),
    INVALID_RECORD_OWNER(HttpStatus.UNAUTHORIZED, "C002", "본인의 달리기 기록이 아닙니다."),
    DUPLICATE_CHECKIN(HttpStatus.BAD_REQUEST, "C003", "24시간 이내에 이미 체크인 하셨습니다."),
    OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "C004", "반경(30m) 밖에서 체크인 할 수 없습니다."),
    NOT_RUNNING_STATUS(HttpStatus.BAD_REQUEST, "C005", "러닝중에만 체크인 할 수 있습니다."),

    // GroupRunning (그룹 러닝 관련)
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "존재하지 않는 Group 입니다"),
    NOT_HOST(HttpStatus.UNAUTHORIZED, "G002", "그룹의 방장이 아닙니다."),
    INVALID_PARTICIPANTS_COUNT(HttpStatus.UNAUTHORIZED, "G003", "인원수 범위를 벗어났습니다."),
    ALREADY_JOINED_GROUP(HttpStatus.BAD_REQUEST, "G004", "이미 참여되어 있습니다."),
    DUPLICATE_GROUP_TIME(HttpStatus.BAD_REQUEST, "G005", "해당 시간에 이미 그룹러닝이 있습니다."),
    NOT_PARTICIPATED(HttpStatus.BAD_REQUEST, "G006", "해당 그룹 참여자가 아닙니다."),
    ALREADY_START_RUNNING(HttpStatus.BAD_REQUEST, "G007", "이미 그룹 러닝이 시작되었습니다."),
    ALREADY_END_RUNNING(HttpStatus.BAD_REQUEST, "G008", "이미 그룹 러닝이 종료되었습니다."),
    NOT_RECRUITING(HttpStatus.BAD_REQUEST, "G009", "모집중인 그룹만 참여 가능합니다."),
    NOT_START_TIME_YET(HttpStatus.BAD_REQUEST, "G010", "아직 그룹러닝 시작 전입니다."),

    // Profile
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "존재하지 않는 프로필 입니다."),

    // Title
    DUPLICATE_TITLE_CODE(HttpStatus.BAD_REQUEST, "T001", "이미 존재하는 칭호 코드입니다."),
    TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "T002", "존재하지 않는 칭호입니다."),
    NOT_YOUR_TITLE(HttpStatus.BAD_REQUEST, "T003", "해당 ProfileTitle 소유권자가 아닙니다."),

    // Shop (상점 관련)
    SHOP_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "SH001", "존재하지 않는 상품입니다."),
    SHOP_ITEM_INACTIVE(HttpStatus.BAD_REQUEST, "SH002", "판매중이 아닌 상품입니다."),
    INSUFFICIENT_POINTS(HttpStatus.BAD_REQUEST, "SH003", "포인트가 부족합니다."),

    // Running (러닝 관련)
    RUN_DISTANCE_TOO_SHORT(HttpStatus.BAD_REQUEST, "R001", "최소 0.15km 이상 달려야 기록이 저장됩니다."),

    // Ghost Run (고스트런 관련)
    GHOST_RANKING_NOT_FOUND(HttpStatus.NOT_FOUND, "GR001", "존재하지 않는 고스트 랭킹입니다."),
    GHOST_START_LOCATION_TOO_FAR(HttpStatus.BAD_REQUEST, "GR002", "고스트 시작 지점 10m 이내에서만 시작할 수 있습니다."),
    GEOCODING_FAILED(HttpStatus.BAD_REQUEST, "GR003", "주소 변환에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
