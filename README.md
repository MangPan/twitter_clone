# Mini Twitter API

Spring Boot로 구현한 미니 트위터 백엔드 프로젝트입니다.  
단순 게시판이 아니라 실제 SNS 백엔드에서 자주 등장하는 **회원가입, 로그인, JWT 인증, 팔로우, 좋아요, 작성자 권한, 프로필 이미지, 게시글 이미지 업로드** 흐름을 연습하기 위한 프로젝트입니다.

## 주요 기능

- id / password 기반 회원가입
- id / password 로그인
- JWT Access Token 발급
- Spring Security 기반 인증 처리
- 현재 로그인 사용자 기반 글 작성/삭제
- 전체 피드 조회
- 팔로잉 타임라인 조회
- 내 글 조회
- 공개 프로필 조회
- 팔로우 / 언팔로우
- 좋아요 등록 / 취소
- 내가 좋아요한 글 조회
- 게시글 작성자만 좋아요 누른 사용자 목록 조회
- MinIO 기반 S3 호환 이미지 업로드
- 프로필 이미지 업로드
- 게시글 이미지 업로드
- seed.sh를 통한 테스트 데이터 생성

## 기술 스택

### Backend

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- JWT
- H2 Database
- AWS SDK S3 Client
- MinIO

### Frontend 테스트용

- Vite
- React
- TypeScript

### Local Infra

- Docker Compose
- MinIO

## 프로젝트 구조 예시

```txt
src/main/java/com/example/minitwitter
├─ auth
│  ├─ controller
│  ├─ dto
│  ├─ exception
│  ├─ jwt
│  ├─ security
│  └─ service
├─ follow
│  ├─ controller
│  ├─ domain
│  ├─ dto
│  ├─ exception
│  ├─ repository
│  └─ service
├─ global
│  ├─ config
│  ├─ domain
│  ├─ exception
│  └─ security
├─ like
│  ├─ controller
│  ├─ domain
│  ├─ dto
│  ├─ exception
│  ├─ repository
│  └─ service
├─ me
│  └─ controller
├─ post
│  ├─ controller
│  ├─ domain
│  ├─ dto
│  ├─ exception
│  ├─ repository
│  └─ service
├─ storage
│  ├─ config
│  ├─ dto
│  ├─ exception
│  └─ service
└─ user
   ├─ controller
   ├─ domain
   ├─ dto
   ├─ exception
   ├─ repository
   └─ service
```

## 실행 전 준비

### 1. MinIO 실행

프로젝트 루트의 `docker-compose.yml` 기준:

```bash
docker compose up -d
```

MinIO 콘솔:

```txt
http://localhost:9001
```

기본 계정:

```txt
ID: minioadmin
PW: minioadmin123
```

S3 API Endpoint:

```txt
http://localhost:9000
```

### 2. MinIO 버킷 공개 읽기 설정

이미지 URL을 브라우저에서 직접 보려면 `mini-twitter` 버킷에 anonymous download/read 권한이 필요합니다.

예시:

```bash
docker run --rm \
  --network mini-twitter-api_default \
  --entrypoint /bin/sh \
  minio/mc -c '
mc alias set local http://minio:9000 minioadmin minioadmin123 &&
mc mb --ignore-existing local/mini-twitter &&
mc anonymous set download local/mini-twitter &&
mc anonymous list local/mini-twitter
'
```

주의:

- 호스트 브라우저에서는 `http://localhost:9000`으로 접근합니다.
- Docker 네트워크 내부 컨테이너에서는 `http://minio:9000`으로 접근합니다.
- `docker run --rm`은 매번 새 컨테이너이므로 alias 설정이 유지되지 않습니다. 그래서 위처럼 한 컨테이너 안에서 연속 실행합니다.

## application.properties 예시

```properties
spring.application.name=mini-twitter-api

spring.datasource.url=jdbc:h2:mem:minitwitter
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.h2.console.enabled=true

spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

jwt.secret=mini-twitter-local-development-secret-key-must-be-long-enough-for-hs256
jwt.access-token-expiration-millis=3600000

storage.s3.endpoint=http://localhost:9000
storage.s3.region=ap-northeast-2
storage.s3.bucket=mini-twitter
storage.s3.access-key=minioadmin
storage.s3.secret-key=minioadmin123
storage.s3.public-base-url=http://localhost:9000/mini-twitter

spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=20MB
```

운영 환경에서는 `jwt.secret`, S3 access key, secret key를 properties에 직접 넣으면 안 됩니다. 환경변수나 secret manager로 분리해야 합니다.

## 백엔드 실행

```bash
./gradlew bootRun
```

헬스 체크:

```bash
curl -i http://localhost:8080/health
```

## 테스트 데이터 생성

`seed.sh`는 JWT 로그인 기반으로 사용자를 만들고, 각 사용자 토큰으로 글/팔로우/좋아요/이미지를 생성합니다.

```bash
chmod +x scripts/seed.sh
./scripts/seed.sh
```

seed는 깨끗한 H2 DB 기준입니다.  
이미 데이터가 있는 상태에서 다시 실행하면 중복 loginId, nickName, follow, like 때문에 실패할 수 있습니다.

권장 흐름:

```bash
# 백엔드 종료
Ctrl + C

# H2 메모리 DB 초기화
./gradlew bootRun

# 다른 터미널에서 seed 실행
./scripts/seed.sh
```

## 테스트 계정

seed 실행 후 사용할 수 있는 계정입니다.

```txt
minjun / password1234
chulsu / password1234
younghee / password1234
backendcat / password1234
frontenddog / password1234
springman / password1234
dbmaster / password1234
deployking / password1234
uxrabbit / password1234
testbear / password1234
```

## 인증 방식

로그인 성공 시 JWT access token을 발급합니다.

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"loginId":"minjun","password":"password1234"}' | jq -r '.accessToken')
```

인증이 필요한 API는 다음 헤더를 포함해야 합니다.

```bash
-H "Authorization: Bearer $TOKEN"
```

## 주요 흐름

### 회원가입

```bash
curl -i -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "minjun",
    "password": "password1234",
    "nickName": "민준",
    "bio": "백엔드 하는 중"
  }'
```

### 로그인

```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "minjun",
    "password": "password1234"
  }'
```

### 내 정보 조회

```bash
curl -i http://localhost:8080/api/me \
  -H "Authorization: Bearer $TOKEN"
```

### 글 작성

```bash
curl -i -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"content":"JWT 기반으로 작성한 글"}'
```

### 프로필 이미지 업로드

```bash
curl -i -X POST http://localhost:8080/api/me/profile-image \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/tmp/test.png"
```

### 게시글 이미지 업로드

```bash
curl -i -X POST http://localhost:8080/api/posts/1/images \
  -H "Authorization: Bearer $TOKEN" \
  -F "files=@/tmp/test.png"
```

### 좋아요

```bash
curl -i -X POST http://localhost:8080/api/posts/1/likes \
  -H "Authorization: Bearer $TOKEN"
```

### 좋아요 취소

```bash
curl -i -X DELETE http://localhost:8080/api/posts/1/likes \
  -H "Authorization: Bearer $TOKEN"
```

### 내 좋아요 목록

```bash
curl -i http://localhost:8080/api/me/likes \
  -H "Authorization: Bearer $TOKEN"
```

### 게시글 좋아요 유저 목록

게시글 작성자만 조회할 수 있습니다.

```bash
curl -i http://localhost:8080/api/posts/1/likes/users \
  -H "Authorization: Bearer $TOKEN"
```

## 이미지 저장 구조

DB에는 실제 이미지 파일을 저장하지 않습니다.

```txt
DB:
- imageUrl
- objectKey
- displayOrder

MinIO:
- 실제 이미지 파일
```

예시 objectKey:

```txt
profile/1/uuid.png
post/3/uuid.jpg
```

URL 예시:

```txt
http://localhost:9000/mini-twitter/profile/1/uuid.png
http://localhost:9000/mini-twitter/post/3/uuid.jpg
```

## 설계 포인트

### 1. 클라이언트가 authorId를 보내지 않음

기존 구조:

```json
{
  "authorId": 1,
  "content": "글"
}
```

현재 구조:

```json
{
  "content": "글"
}
```

작성자는 JWT에서 추출한 현재 사용자로 결정합니다.

### 2. requesterId를 쿼리로 받지 않음

기존 구조:

```txt
DELETE /api/posts/{id}?requesterId=1
```

현재 구조:

```txt
DELETE /api/posts/{id}
Authorization: Bearer <token>
```

삭제 요청자는 JWT에서 가져오고, 게시글 작성자와 비교합니다.

### 3. 좋아요를 단순 count가 아니라 관계 엔티티로 관리

`PostLike` 엔티티를 사용합니다.

```txt
PostLike
- id
- post
- user
- createdAt
```

이를 통해 다음 기능이 가능합니다.

- 중복 좋아요 방지
- 좋아요 취소
- 내가 좋아요한 글 조회
- 게시글에 좋아요 누른 사용자 목록 조회

### 4. 작성자만 접근 가능한 데이터

게시글에 좋아요 누른 사용자 목록은 게시글 작성자만 볼 수 있습니다.

```txt
GET /api/posts/{postId}/likes/users
```

서비스 계층에서 다음 조건을 검사합니다.

```txt
post.author.id == currentUserId
```

### 5. Spring 경유 이미지 업로드

현재 이미지는 다음 흐름으로 업로드됩니다.

```txt
브라우저/앱
→ Spring Boot
→ MinIO
```

이 방식은 구현이 단순하고 인증/인가 검사가 쉽습니다.  
단, 업로드 트래픽은 Spring 서버를 거칩니다.

장기적으로는 presigned URL 방식도 고려할 수 있습니다.

```txt
브라우저/앱
→ Spring Boot: 업로드 URL 요청
→ S3/MinIO 직접 업로드
→ Spring Boot: 업로드 완료 등록
```

## 개발 중 겪은 주요 이슈

### 1. 주소창에서는 되는데 React fetch는 실패

주소창 접근:

```txt
http://localhost:8080/api/posts/feed
```

React fetch:

```txt
http://localhost:5173 → http://localhost:8080
```

포트가 다르면 origin이 다릅니다.  
개발 환경에서는 Vite proxy를 사용하면 안정적입니다.

`vite.config.ts` 예시:

```ts
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
```

프론트의 API base URL은 상대 경로로 둡니다.

```ts
const API_BASE_URL = "";
```

### 2. MinIO objectKey가 null로 저장됨

원인:

```txt
StorageService.uploadImage(file, directory, maxSizeBytes)
```

에서 `directory`가 null로 들어감.

해결:

- Controller에서 directory를 명확히 전달
- StorageService에서 directory 검증 추가

예시:

```txt
profile/{userId}
post/{postId}
```

### 3. MinIO URL 직접 접근 실패

원인:

```txt
버킷 기본 정책이 private
```

해결:

```txt
mini-twitter 버킷에 anonymous download/read 정책 설정
```

### 4. mc 컨테이너에서 localhost 접근 실패

Docker 네트워크 내부에서:

```txt
localhost = 해당 컨테이너 자신
```

MinIO 컨테이너에 접근하려면 compose 서비스명을 사용합니다.

```txt
http://minio:9000
```

호스트 브라우저에서는:

```txt
http://localhost:9000
```

## 정리 상태

삭제해도 되는 테스트 코드:

```txt
src/main/java/com/example/minitwitter/storage/controller/StorageTestController.java
```

실제 API가 완성되었으므로 테스트용 업로드 컨트롤러는 제거하는 편이 좋습니다.

## 다음 개선 후보

- Refresh Token
- 회원 정보 수정 API
- 게시글 수정 API
- 게시글 이미지 삭제 API
- S3 object 삭제 처리
- orphan object 정리 배치
- presigned URL 업로드
- PostgreSQL 전환
- 테스트 코드 추가
- PostResponse에 `likedByMe` 추가
- 프론트 좋아요 상태 개선
