# Frun 백엔드 개발 가이드 (Spring Boot)

## 프로젝트 개요

- **서비스명**: Frun (소셜 러닝 일지 웹 서비스)
- **대상 시스템**: FO (Frun) / BO (Pandora)
- **서비스 형태**: REST API (JSON)

---

## 기술 스택

- **Framework**: Spring Boot 3.x
- **Language**: Java 21
- **ORM**: JPA / QueryDSL
- **Database**: MySQL (또는 Oracle)
- **Cache**: Redis (Refresh Token 저장)
- **인증**: OAuth2 (네이버), JWT (Access Token / Refresh Token)
- **빌드**: Gradle

---

## 패키지 구조

```
src/main/java/com/frun/
├── domain/
│   ├── member/          # 회원
│   ├── running/         # 러닝일지
│   ├── comment/         # 댓글/답글
│   ├── friend/          # 친구
│   ├── notification/    # 알림
│   ├── report/          # 신고
│   └── notice/          # 공지사항
├── global/
│   ├── auth/            # JWT, OAuth2 설정
│   ├── config/          # Security, Redis, S3 설정
│   ├── exception/       # 공통 예외 처리
│   └── response/        # 공통 응답 형식
└── FrunApplication.java
```

---

## 인증 / 보안

### OAuth2 + JWT 흐름

```
1. 클라이언트 → 네이버 OAuth2 로그인 요청
2. 네이버 → 인가 코드 발급
3. 백엔드 → 네이버 Access Token 교환
4. 백엔드 → 회원 정보 조회 (이름, 이메일, provider_id)
5. 신규 회원: DB 저장 후 닉네임 설정 필요 플래그 응답
6. JWT Access Token + Refresh Token 발급
   - Refresh Token: Redis에 저장 (key: memberId)
7. 로그아웃 시 Redis에서 Refresh Token 삭제
```

### 회원 상태 처리

| 상태 | 로그인 시 동작 |
|------|-------------|
| 활성화 | 정상 로그인 |
| 정지 | 정지 안내 메시지 반환 |
| 비활성화 | 계정 활성화 동의 메시지 반환 |
| 탈퇴 | 로그인 불가 |

---

## API 설계

### 공통 응답 형식

```json
{
  "success": true,
  "data": { },
  "message": "처리되었습니다."
}
```

---

### 회원 API

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/oauth2/authorization/naver` | 네이버 로그인 리다이렉트 |
| POST | `/api/v1/auth/nickname` | 닉네임 설정 (최초 로그인) |
| POST | `/api/v1/auth/logout` | 로그아웃 (Redis Refresh Token 삭제) |
| POST | `/api/v1/auth/refresh` | Access Token 재발급 |
| GET | `/api/v1/members/me` | 내 정보 조회 |
| PATCH | `/api/v1/members/me` | 회원 정보 수정 (닉네임, 프로필 사진) |
| PATCH | `/api/v1/members/me/deactivate` | 계정 비활성화 |
| GET | `/api/v1/members/nickname/check` | 닉네임 중복 확인 |

#### 닉네임 설정 (`POST /api/v1/auth/nickname`)
- 닉네임 설정 화면에서 프로필 사진도 설정 가능
  - 이미지 파일 최대 3MB
  - jpg, jpeg, png 형식만 가능

---

### 러닝일지 API

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/v1/running-logs` | 일지 작성 |
| GET | `/api/v1/running-logs` | 내 일지 목록 (무한스크롤) |
| GET | `/api/v1/running-logs/{logId}` | 일지 상세 조회 |
| PUT | `/api/v1/running-logs/{logId}` | 일지 수정 |
| DELETE | `/api/v1/running-logs/{logId}` | 일지 삭제 |
| GET | `/api/v1/members/{memberId}/running-logs` | 친구 일지 목록 |

#### 일지 작성 요청 Body

```json
{
  "runDate": "2025-03-01",
  "runTime": "07:30",
  "distanceKm": 5.2,
  "durationMin": 28,
  "durationSec": 30,
  "pace": "5'23\"",
  "memo": "오늘도 완주!",
  "isPublic": true,
  "imageUrls": ["https://s3.../image1.jpg"]
}
```

#### 목록 조회 Query Parameter

```
?sort=LATEST | FAST_PACE | LONG_DISTANCE
&cursor=마지막ID
&size=10
```

---

### 좋아요 API

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/v1/running-logs/{logId}/like` | 좋아요 |
| DELETE | `/api/v1/running-logs/{logId}/like` | 좋아요 취소 |

---

### 댓글/답글 API

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/v1/running-logs/{logId}/comments` | 댓글 작성 |
| GET | `/api/v1/running-logs/{logId}/comments` | 댓글 목록 (무한스크롤) |
| PUT | `/api/v1/comments/{commentId}` | 댓글 수정 |
| DELETE | `/api/v1/comments/{commentId}` | 댓글 삭제 |
| POST | `/api/v1/comments/{commentId}/replies` | 답글 작성 |
| GET | `/api/v1/comments/{commentId}/replies` | 답글 목록 (10개씩) |
| PUT | `/api/v1/replies/{replyId}` | 답글 수정 |
| DELETE | `/api/v1/replies/{replyId}` | 답글 삭제 |

---

### 신고 API

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/v1/running-logs/{logId}/reports` | 러닝일지 신고 |
| POST | `/api/v1/comments/{commentId}/reports` | 댓글/답글 신고 |

#### 신고 요청 Body

```json
{
  "reason": "INAPPROPRIATE | ETC",
  "etcReason": "기타 사유 (ETC인 경우 필수)"
}
```

---

### 러닝 통계 API

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/v1/stats/me` | 내 통계 조회 |
| GET | `/api/v1/stats/members/{memberId}` | 친구 통계 조회 |

#### 통계 Query Parameter

```
?type=WEEKLY | MONTHLY | PERIOD
&startDate=2025-01-01   (PERIOD인 경우)
&endDate=2025-03-01     (PERIOD인 경우)
```

---

### 친구 API

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/v1/friends/request` | 친구 요청 |
| PATCH | `/api/v1/friends/{requestId}/accept` | 친구 수락 |
| PATCH | `/api/v1/friends/{requestId}/reject` | 친구 거절 |
| DELETE | `/api/v1/friends/{memberId}` | 친구 삭제 |
| GET | `/api/v1/friends` | 친구 목록 조회 |
| GET | `/api/v1/members/search` | 닉네임 검색 |

---

### 알림 API

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/v1/notifications` | 알림 목록 조회 |
| PATCH | `/api/v1/notifications/{notificationId}/read` | 알림 읽음 처리 |

---

### 공지사항 API (FO)

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/v1/notices` | 공지사항 목록 (최신순) |
| GET | `/api/v1/notices/{noticeId}` | 공지사항 상세 |

---

### BO API (Pandora - 관리자 전용)

#### 회원 관리

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/bo/v1/members` | 회원 목록 조회 (검색/필터) |
| GET | `/api/bo/v1/members/{memberId}` | 회원 상세 조회 |
| POST | `/api/bo/v1/members/{memberId}/sanction` | 회원 제재 |
| DELETE | `/api/bo/v1/members/{memberId}/sanction` | 제재 해제 |
| DELETE | `/api/bo/v1/members/{memberId}` | 회원 강제 탈퇴 |

#### 신고 관리

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/bo/v1/reports/running-logs` | 신고된 러닝일지 목록 |
| POST | `/api/bo/v1/reports/running-logs/{reportId}/process` | 신고 처리 (경고/삭제/반려) |
| GET | `/api/bo/v1/reports/comments` | 신고된 댓글/답글 목록 |
| POST | `/api/bo/v1/reports/comments/{reportId}/process` | 신고 처리 |

#### 공지사항 관리

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/bo/v1/notices` | 공지사항 목록 |
| POST | `/api/bo/v1/notices` | 공지사항 등록 |
| PUT | `/api/bo/v1/notices/{noticeId}` | 공지사항 수정 |
| DELETE | `/api/bo/v1/notices/{noticeId}` | 공지사항 삭제 |

---

## 도메인 모델 (핵심 엔티티)

### USER

| 컬럼 | 타입 | 설명 |
|------|------|------|
| user_id | BIGINT PK NOT NULL | |
| provider_id | VARCHAR(255) NULL | 소셜 제공자 고유 ID |
| email | VARCHAR(100) NOT NULL | 소셜 계정 이메일 |
| user_name | VARCHAR(50) NOT NULL | 소셜 계정 실명 |
| nick_name | VARCHAR(60) NOT NULL | 닉네임 |
| user_status | VARCHAR(20) NOT NULL | ACTIVE (기본값) |
| image_url | VARCHAR(500) NULL | 프로필 이미지 URL |
| social_type_id | BIGINT NOT NULL FK | SOCIAL_LOGIN_TYPE 참조 |
| created_at | DATETIME NOT NULL | 가입일 |
| updated_at | DATETIME NULL | |

### SOCIAL_LOGIN_TYPE

| 컬럼 | 타입 | 설명 |
|------|------|------|
| social_type_id | BIGINT PK NOT NULL | |
| type_value | VARCHAR(30) NOT NULL | ex) NAVER, GOOGLE |
| type_name | VARCHAR(30) NOT NULL | ex) 네이버, 구글 |

### RUNNING_LOG

| 컬럼 | 타입 | 설명 |
|------|------|------|
| running_log_id | BIGINT PK NOT NULL | |
| user_id | BIGINT NOT NULL FK | USER 참조 |
| run_date | DATE NOT NULL | 운동 날짜 |
| duration | TIME NOT NULL | 운동 시간 |
| distance | DECIMAL(5,2) NOT NULL | 거리 (km) |
| pace | VARCHAR(10) NOT NULL | 페이스 |
| is_public | BOOLEAN NOT NULL | 공개 여부 |
| memo | TEXT NULL | 메모 |
| is_deleted | BOOLEAN NOT NULL | 삭제 여부 (기본값 FALSE) |
| created_at | DATETIME NOT NULL | |
| updated_at | DATETIME NULL | |

### RUNNING_LOG_IMAGE

| 컬럼 | 타입 | 설명 |
|------|------|------|
| log_image_id | BIGINT PK NOT NULL | |
| running_log_id | BIGINT NOT NULL FK | RUNNING_LOG 참조 |
| image_url | VARCHAR(500) NOT NULL | 이미지 URL |
| created_at | DATETIME NOT NULL | |

### LIKE

| 컬럼 | 타입 | 설명 |
|------|------|------|
| like_id | BIGINT PK NOT NULL | |
| user_id | BIGINT NOT NULL FK | USER 참조 |
| running_log_id | BIGINT NOT NULL FK | RUNNING_LOG 참조 |
| created_at | DATETIME NOT NULL | |

### COMMENT

| 컬럼 | 타입 | 설명 |
|------|------|------|
| comment_id | BIGINT PK NOT NULL | |
| running_log_id | BIGINT NOT NULL FK | RUNNING_LOG 참조 |
| user_id | BIGINT NOT NULL FK | USER 참조 |
| content | TEXT NOT NULL | 댓글 내용 |
| is_deleted | BOOLEAN NOT NULL | 삭제 여부 (기본값 FALSE) |
| created_at | DATETIME NOT NULL | |
| updated_at | DATETIME NULL | |

### FRIEND_REQUEST

| 컬럼 | 타입 | 설명 |
|------|------|------|
| friend_request_id | BIGINT PK NOT NULL | |
| sender_id | BIGINT NOT NULL FK | 요청자 (USER 참조) |
| receiver_id | BIGINT NOT NULL FK | 수신자 (USER 참조) |
| status | VARCHAR(10) NOT NULL | PENDING (기본값) / ACCEPTED / REJECTED |
| created_at | DATETIME NOT NULL | |

### FRIENDSHIP

| 컬럼 | 타입 | 설명 |
|------|------|------|
| recieve_user_id | BIGINT PK NOT NULL FK | 수신자 (USER 참조) |
| sender_user_id | BIGINT NOT NULL FK | 발신자 (USER 참조) |
| created_at | DATETIME NOT NULL | |

### NOTIFICATION

| 컬럼 | 타입 | 설명 |
|------|------|------|
| notification_id | BIGINT PK NOT NULL | |
| user_id | BIGINT NOT NULL FK | 알림 수신자 (USER 참조) |
| type | VARCHAR(30) NOT NULL | 알림 유형 |
| is_read | BOOLEAN NOT NULL | 읽음 여부 |
| friend_request_id | BIGINT NULL FK | FRIEND_REQUEST 참조 |
| comment_id | BIGINT NULL FK | COMMENT 참조 |
| created_at | DATETIME NOT NULL | |

### REPORT

| 컬럼 | 타입 | 설명 |
|------|------|------|
| report_id | BIGINT PK NOT NULL | |
| user_id | BIGINT NOT NULL FK | 신고자 (USER 참조) |
| report_reason | TEXT NOT NULL | 신고 사유 |
| status | VARCHAR(30) NOT NULL | PENDING / COMPLETED / REJECTED |
| report_type_id | BIGINT NOT NULL FK | REPORT_TYPE 참조 |
| running_log_id | BIGINT NULL FK | 신고 대상 러닝일지 |
| comment_id | BIGINT NULL FK | 신고 대상 댓글 |
| created_at | DATETIME NOT NULL | |

### REPORT_TYPE

| 컬럼 | 타입 | 설명 |
|------|------|------|
| report_type_id | BIGINT PK NOT NULL | |
| type_value | VARCHAR(30) NOT NULL | 신고 유형 값 |
| type_name | VARCHAR(30) NOT NULL | 신고 유형 이름 |

### REPORT_ACTION

| 컬럼 | 타입 | 설명 |
|------|------|------|
| report_id | BIGINT PK NOT NULL FK | REPORT 참조 (1:1) |
| action_type | VARCHAR(30) NOT NULL | 처리 유형 |
| action_reason | TEXT NOT NULL | 처리 사유 |
| created_at | DATETIME NOT NULL | |

### NOTICE

| 컬럼 | 타입 | 설명 |
|------|------|------|
| notice_id | BIGINT PK NOT NULL | |
| title | VARCHAR(300) NOT NULL | 제목 |
| content | TEXT NOT NULL | 내용 |
| created_at | DATETIME NOT NULL | |
| updated_at | DATETIME NULL | |

---

## 비즈니스 규칙 요약

### 러닝일지
- 페이스 = 러닝시간(분) / 거리(km) → 저장 전 서버에서도 검증 권장
- 사진은 S3 업로드 후 URL 저장 (최대 5개)

### 친구
- FRIENDSHIP 테이블은 양방향 저장 (A→B, B→A 두 row)
- 삭제 시 양방향 row 모두 삭제

### 비활성화 자동 탈퇴
- 스케줄러로 비활성화 후 3개월 경과 회원 자동 탈퇴 처리

### BO 권한
- `/api/bo/**` 경로는 ROLE_ADMIN만 접근 가능
- Spring Security에서 경로별 권한 설정

### 신고 처리
- 경고: 작성자 경고 횟수 증가
- 삭제: 해당 일지/댓글 강제 삭제
- 반려: 신고 status → REJECTED

---

## 환경 설정

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/frun
  redis:
    host: localhost
    port: 6379
  security:
    oauth2:
      client:
        registration:
          naver:
            client-id: ${NAVER_CLIENT_ID}
            client-secret: ${NAVER_CLIENT_SECRET}

jwt:
  secret: ${JWT_SECRET}
  access-expiration: 1800000    # 30분
  refresh-expiration: 604800000 # 7일

cloud:
  aws:
    s3:
      bucket: ${S3_BUCKET}
```
