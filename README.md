# 🏃‍♀️‍➡️️ Baton - 소셜 러닝 트래킹 플랫폼 (Backend)

> **"혼자 뛰지 말고, 같이 뛰자!"**
> 러닝 기록을 측정하고, 주변 러너들과 그룹을 맺어 함께 달릴 수 있는 소셜 러닝 애플리케이션의 백엔드 API 서버입니다.

## 🎯 Project Overview
- **프로젝트명:** Baton (러닝앱)
- **개발 기간:** 2026.04 ~ 진행 중
- **주요 목적:** 러너들의 동기 부여를 위한 레벨과 칭호(Title) 시스템, 실시간 그룹 러닝 매칭 기능 제공.

## 🛠️ Tech Stack
 
### Backend
- **Java 17** / **Spring Boot 3.x**
- **Spring Security** + **JWT** (Stateless 인증 아키텍처)
- **WebSocket** (실시간 기능 연동)
  
### Database & ORM
- **MySQL 8.0**
- **Spring Data JPA** (데이터 영속성 관리)
- **QueryDSL** (동적 쿼리 및 복잡한 통계 데이터 추출)
- **Redis** (Refresh Token 관리 및 캐싱 - *선택/예정*)

### DevOps & Infrastructure
- **AWS EC2 / RDS** (클라우드 호스팅)
- **GitHub Actions** (CI/CD 자동 배포 파이프라인 구축)

## 🏗️ Core Architecture & Features

1. **사용자 및 권한 관리 (Auth & Profile)**
    - JWT 기반의 Access/Refresh Token 발급 및 안전한 클레임 파싱.
    - Spring ArgumentResolver를 활용한 `@LoginMember` 커스텀 애노테이션으로 세션 리스(Sessionless) 환경에서의 객체 지향적 유저 바인딩.

2. **칭호(Title) 및 업적 시스템**
    - 사용자 러닝 데이터 기반의 자동 칭호 부여.
    - 마스터 데이터(Title)와 사용자 매핑 테이블(ProfileTitle)을 분리한 도메인 주도 설계.
    - Java Record를 DTO로 활용하여 불변성 보장 및 Stream API를 통한 고속 데이터 매핑.

3. **러닝 그룹 매칭 및 실시간 통신 (Group & WebSocket)**
    - 특정 지역/시간대의 러닝 그룹 모집글 작성 및 참여 기능.
    - WebSocket을 활용한 실시간 알림/상태 공유 기능 (현재 통합 테스트 중).

## 🚀 Getting Started

동료 프론트엔드 개발자 및 백엔드 기여자를 위한 로컬 실행 가이드입니다.

### Prerequisites
- JDK 17+ 
- MySQL 8.0+
- Gradle

### Local Setup Steps

1. **Clone the repository**
   ```bash
   git clone [https://github.com/](https://github.com/)[github-id]/runapp-backend.git
   cd runapp-backend       