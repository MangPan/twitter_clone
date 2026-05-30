# Mini Twitter Backend Architecture Notes

## 목표

이 프로젝트는 단순 CRUD를 넘어서 실제 SNS 백엔드에서 자주 쓰이는 패턴을 작은 규모로 연습하기 위한 프로젝트입니다.

핵심 학습 목표:

```txt
인증
인가
현재 사용자
관계형 도메인
이미지 스토리지
권한 기반 데이터 접근 제한
```

## 도메인 요약

### User

사용자 계정과 프로필 정보를 담당합니다.

```txt
User
- id
- loginId
- password
- nickName
- bio
- profileImageUrl
- createdAt
- updatedAt
```

비밀번호는 BCrypt로 암호화하여 저장합니다.

### Post

게시글을 담당합니다.

```txt
Post
- id
- author
- content
- likeCount
- createdAt
- updatedAt
```

글 작성자는 클라이언트가 보내지 않고 JWT에서 추출한 현재 사용자로 결정합니다.

### PostImage

게시글 이미지를 담당합니다.

```txt
PostImage
- id
- post
- imageUrl
- objectKey
- displayOrder
- createdAt
- updatedAt
```

이미지 바이너리는 DB에 저장하지 않고 MinIO에 저장합니다.

### Follow

팔로우 관계를 담당합니다.

```txt
Follow
- id
- follower
- following
- createdAt
```

### PostLike

좋아요 관계를 담당합니다.

```txt
PostLike
- id
- post
- user
- createdAt
```

`post_id + user_id` unique 제약으로 중복 좋아요를 막습니다.

## 인증 구조

로그인 성공 시 JWT Access Token을 발급합니다.

```txt
POST /api/auth/login
→ accessToken 반환
```

클라이언트는 이후 요청마다 헤더에 토큰을 담습니다.

```txt
Authorization: Bearer <token>
```

서버에서는 `JwtAuthenticationFilter`가 다음을 처리합니다.

```txt
Authorization 헤더 확인
→ Bearer token 추출
→ 토큰 검증
→ userId 추출
→ SecurityContext에 Authentication 저장
```

서비스/컨트롤러에서는 `CurrentUser` 컴포넌트를 통해 현재 사용자 id를 가져옵니다.

```txt
currentUser.getId()
```

## 인가 구조

인가 판단은 주로 Service 계층에서 처리합니다.

예시:

```txt
게시글 삭제
→ post.author.id == currentUserId 인지 확인

게시글 이미지 업로드
→ post.author.id == currentUserId 인지 확인

게시글 좋아요 유저 목록 조회
→ post.author.id == currentUserId 인지 확인
```

## 공개 API와 보호 API

### 공개 가능

```txt
회원가입
로그인
사용자 목록
공개 프로필
전체 피드
게시글 단건
공개 팔로워/팔로잉 목록
```

### 인증 필요

```txt
내 정보
내 글
내 타임라인
내 좋아요 목록
글 작성
글 삭제
프로필 이미지 업로드
게시글 이미지 업로드
팔로우/언팔로우
좋아요/좋아요 취소
좋아요 유저 목록 조회
```

## 이미지 저장 구조

현재는 Spring 경유 업로드 방식입니다.

```txt
Client
→ Spring Boot
→ MinIO
```

장점:

```txt
구현이 단순함
Spring에서 인증/인가 가능
파일 타입/크기 검증 가능
DB 업데이트와 흐름을 함께 처리하기 쉬움
```

단점:

```txt
업로드 트래픽이 Spring 서버를 거침
대용량 업로드에 불리함
```

장기적으로는 presigned URL 방식을 고려할 수 있습니다.

```txt
Client
→ Spring Boot: 업로드 URL 요청
→ Object Storage 직접 업로드
→ Spring Boot: 업로드 완료 등록
```

## MinIO와 S3 호환 개념

MinIO는 S3 호환 오브젝트 스토리지입니다.

중요 개념:

```txt
bucket: 파일을 담는 최상위 공간
objectKey: 파일 경로처럼 보이는 객체 이름
public URL: 브라우저에서 접근 가능한 URL
objectKey != 실제 폴더
```

예시:

```txt
bucket: mini-twitter
objectKey: post/1/uuid.png
url: http://localhost:9000/mini-twitter/post/1/uuid.png
```

## 프론트 개발 환경

프론트는 Vite 개발 서버를 사용합니다.

프론트:

```txt
http://localhost:5173
```

백엔드:

```txt
http://localhost:8080
```

포트가 다르면 브라우저 기준 origin이 다릅니다.  
개발 환경에서는 Vite proxy를 사용하면 편합니다.

```ts
server: {
  proxy: {
    "/api": {
      target: "http://localhost:8080",
      changeOrigin: true,
    },
  },
}
```

프론트 코드에서는 상대 경로를 사용합니다.

```ts
const API_BASE_URL = "";
```

## 현재 한계

### Refresh Token 없음

현재는 Access Token만 사용합니다.  
실서비스라면 Refresh Token, 만료 처리, 로그아웃 처리, 토큰 재발급 흐름이 필요합니다.

### 이미지 삭제 없음

게시글 이미지 업로드는 가능하지만 이미지 삭제 API는 없습니다.  
게시글 삭제 시 S3 object도 같이 삭제할지 정책이 필요합니다.

### orphan object 정리 없음

파일 업로드는 성공했지만 DB 저장이 실패하거나, DB 기록이 삭제되었지만 S3 object가 남는 경우를 정리하는 배치가 없습니다.

### 좋아요 상태 응답 없음

현재 PostResponse에 `likedByMe`가 없습니다.  
프론트에서 좋아요 버튼 상태를 정확하게 표시하려면 로그인 사용자 기준 좋아요 여부가 필요합니다.

### PostgreSQL 미전환

현재 H2 기반입니다.  
실서비스에 가깝게 하려면 PostgreSQL로 전환하는 것이 좋습니다.

## 추천 다음 작업

우선순위:

```txt
1. StorageTestController 제거
2. README/API 문서 반영
3. 프론트와 백엔드 API 경로 최종 정합성 확인
4. PostResponse에 likedByMe 추가 검토
5. PostgreSQL 전환
6. 테스트 코드 추가
7. presigned URL 업로드 연습
```
