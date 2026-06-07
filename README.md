# 🏃‍♀️‍➡️️ Baton - 소셜 러닝 트래킹 플랫폼 (Backend)

> **"혼자 뛰지 말고, 같이 뛰자!"**
> 러닝 기록을 측정하고, 주변 러너들과 그룹을 맺어 함께 달릴 수 있는 소셜 러닝 애플리케이션의 백엔드 API 서버입니다.

## 🎯 Project Overview
- **프로젝트명:** Baton (러닝앱)
- **개발 기간:** 2026.04 ~ 진행 중
- **주요 목적:** 러너들의 동기 부여를 위한 레벨·칭호(Title) 시스템, 실시간 그룹 러닝 매칭, 스팟 체크인, 캐릭터 커스터마이징 기능 제공.

## 🛠️ Tech Stack

### Backend
- **Java 17** / **Spring Boot 3.4.1**
- **Spring Security** + **JWT** (jjwt 0.11.5, Stateless 인증 아키텍처)
- **WebSocket + STOMP** (그룹 러닝 실시간 위치 공유)
- **QueryDSL 5.0** (동적 쿼리)

### Database & ORM
- **PostgreSQL + PostGIS** (공간 데이터 / 위치 기반 쿼리)
- **Spring Data JPA** + **Hibernate Spatial** (JTS Point / LineString 매핑)
- **Redis** (Refresh Token 저장, Pub/Sub 실시간 위치 브로드캐스트)

### DevOps & Infrastructure
- **GitHub Actions** (CI/CD — PostGIS·Redis 서비스 컨테이너로 테스트 후 자동 배포)
- **Docker Compose** (로컬 PostgreSQL+PostGIS, Redis 실행)
- **Swagger / SpringDoc OpenAPI 2.8.5** (API 문서, `/swagger-ui.html`)

## 🏗️ Core Architecture & Features

1. **사용자 및 권한 관리 (Auth & Member)**
   - JWT 기반 Access/Refresh Token 발급. Refresh Token은 Redis에 저장, 로그아웃된 Access Token은 Redis 블랙리스트 등록.
   - Spring ArgumentResolver + `@LoginMember` 커스텀 애노테이션으로 Stateless 환경에서 컨트롤러 파라미터에 `Member` 객체 직접 주입.
   - 주요 엔드포인트: 회원가입, 로그인, 로그아웃, 내 정보 조회(`GET /api/v1/member/me`), 비밀번호 변경, 회원 탈퇴.

2. **러닝 기록 (Personal Running)**
   - 러닝 시작·종료 API, GPS 좌표 리스트를 PostGIS `LineString`으로 저장.
   - `DistanceUtils`: 하버사인 공식으로 좌표 간 거리 계산(미터). `GeometryUtils`: `List<LatLng>` → JTS `LineString` 변환.
   - 러닝 종료 시 페이스·거리 통계 자동 갱신 및 경험치 지급.

3. **레벨 및 칭호(Title) 시스템**
   - 누적 경험치 기반 레벨 자동 산정 (`LevelCalculator`, 식: `exp = 250n² + 250n`).
   - 마스터 데이터(`Title`) — 사용자 매핑(`ProfileTitle`) 분리 설계. 칭호 획득·장착 API 제공.

4. **그룹 러닝 매칭 및 실시간 통신**
   - 모집글 생성·수정·삭제·참여·나가기, 상태: `RECRUITING → RUNNING → COMPLETED / CANCELED`.
   - 스케줄러(`GroupRunningScheduler`)가 매 정각 DB 벌크 상태 업데이트.
   - WebSocket(`/ws/run`) + STOMP 구독(`/topic/group/{groupId}`)으로 실시간 위치 공유. Redis Pub/Sub → `RedisSubscriber` → 브로드캐스트.
   - `GET /api/v1/groups/{groupId}/members/colors` — WebSocket 채널 입장 시 참여자 캐릭터 색상 일괄 조회.

5. **스팟 체크인**
   - 주변 러닝 스팟 조회 (PostGIS 반경 쿼리), 30m 이내 체크인 판정 (`LocationUtils`).
   - 체크인 쿨타임 확인 API (`GET /api/v1/spots/cooldowns`), 체크인 시 포인트·경험치 지급.

6. **소셜 (프로필·팔로우·피드)**
   - 닉네임 검색, 팔로우 요청·수락·거절, 팔로워/팔로잉 목록, 러닝 피드 조회.
   - `GET /api/v1/profile/colors?memberIds=1,2,3` — 전체 WebSocket 채널에서 주변 유저 ID 기반 캐릭터 색상 배치 조회.

7. **상점 및 포인트**
   - 포인트로 캐릭터 색상(코어 컬러) 아이템 구매, 장착.
   - 포인트 적립 이력 조회.

8. **랭킹**
   - 거리 기반 주간·전체 랭킹 조회.

9. **어드민**
   - 스팟 CRUD, 회원 조회, 공지사항 CRUD.

## 🚀 Getting Started

### Prerequisites
- JDK 17+
- Docker & Docker Compose (PostgreSQL+PostGIS, Redis)
- Gradle

### 환경 변수 설정

```bash
DB_URL=jdbc:postgresql://localhost:5432/{DB_NAME}
DB_USERNAME=...
DB_PASSWORD=...
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=...
JWT_ACCESS_TOKEN_TIME=...    # 밀리초
JWT_REFRESH_TOKEN_TIME=...   # 밀리초
DEPLOY_SECRET=...
```

### Local Setup

```bash
# 1. 인프라 실행
docker-compose up -d

# 2. 빌드
./gradlew build

# 3. 실행
./gradlew bootRun
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

### 테스트

```bash
./gradlew clean test

# 단일 테스트
./gradlew test --tests "com.running.runapp.domain.running.util.DistanceUtilsTest"
```

## 📁 Package Structure

```
com.running.runapp
├── domain/
│   ├── admin/        # 어드민 - 스팟 CRUD, 회원 조회, 공지사항
│   ├── groupRunning/ # 그룹 러닝 모집/참여/실시간 시작·종료
│   ├── member/       # 회원가입, 로그인, 내 정보 조회
│   ├── myroom/       # 내 방 - 장착된 칭호·컬러 조회
│   ├── point/        # 포인트 적립 이력
│   ├── profile/      # 프로필, 팔로우, 피드, 칭호, 색상 배치 조회
│   ├── ranking/      # 랭킹
│   ├── running/      # 러닝 시작·종료·기록 (개인)
│   ├── shop/         # 아이템 상점 (포인트 소비)
│   └── spot/         # 러닝 스팟 체크인, 주변 스팟 조회
└── global/
    ├── common/       # ApiResponse, BaseTimeEntity, LevelCalculator, LocationUtils
    ├── config/       # WebSocket, Redis, QueryDSL, Swagger, WebConfig
    ├── deploy/       # 배포 웹훅 수신 엔드포인트
    ├── error/        # BusinessException, ErrorCode, GlobalExceptionHandler
    ├── init/         # DataInitializer (어드민·기본 칭호·샘플 스팟 초기화)
    └── security/     # JWT, SecurityConfig, PrincipalDetails
```

## 🔄 CI/CD

`develop` 브랜치 push 시 GitHub Actions가:
1. PostGIS + Redis 서비스 컨테이너를 띄워 `./gradlew clean test` 실행
2. 테스트 통과 시 `DEPLOY_URL`로 POST 요청 → 서버 자동 배포 (`DeployController` 수신, `X-Deploy-Secret` 헤더로 인증)
