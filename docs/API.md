# Mini Twitter API Summary

Base URL:

```txt
http://localhost:8080
```

인증이 필요한 API는 다음 헤더를 사용합니다.

```txt
Authorization: Bearer <accessToken>
```

## 공통 에러 응답

```json
{
  "code": "ERROR_CODE",
  "message": "에러 메시지",
  "errors": []
}
```

## Auth API

### 로그인

```txt
POST /api/auth/login
```

Request:

```json
{
  "loginId": "minjun",
  "password": "password1234"
}
```

Response:

```json
{
  "accessToken": "eyJ...",
  "tokenType": "Bearer",
  "userId": 1,
  "loginId": "minjun",
  "nickName": "민준",
  "profileImageUrl": "http://localhost:9000/mini-twitter/profile/1/uuid.png"
}
```

## User API

### 회원가입

```txt
POST /api/users
```

Request:

```json
{
  "loginId": "minjun",
  "password": "password1234",
  "nickName": "민준",
  "bio": "백엔드 하는 중"
}
```

Response:

```json
{
  "id": 1,
  "loginId": "minjun",
  "nickName": "민준",
  "bio": "백엔드 하는 중",
  "profileImageUrl": null,
  "createdAt": "2026-05-31T01:00:00",
  "updatedAt": "2026-05-31T01:00:00"
}
```

### 사용자 목록 조회

```txt
GET /api/users
```

Response:

```json
[
  {
    "id": 1,
    "loginId": "minjun",
    "nickName": "민준",
    "bio": "백엔드 하는 중",
    "profileImageUrl": null,
    "createdAt": "...",
    "updatedAt": "..."
  }
]
```

### 사용자 단건 조회

```txt
GET /api/users/{id}
```

### 닉네임으로 사용자 조회

```txt
GET /api/users/by-nickname/{nickName}
```

JWT 로그인 구조에서는 필수 API는 아니지만 테스트 편의용으로 유지할 수 있습니다.

### 공개 프로필 조회

```txt
GET /api/users/{id}/profile
```

Response:

```json
{
  "id": 1,
  "loginId": "minjun",
  "nickName": "민준",
  "bio": "백엔드 하는 중",
  "profileImageUrl": "http://localhost:9000/mini-twitter/profile/1/uuid.png",
  "postCount": 3,
  "followerCount": 2,
  "followingCount": 4
}
```

## Me API

### 내 정보 조회

```txt
GET /api/me
```

Auth: required

Response:

```json
{
  "id": 1,
  "loginId": "minjun",
  "nickName": "민준",
  "bio": "백엔드 하는 중",
  "profileImageUrl": "http://localhost:9000/mini-twitter/profile/1/uuid.png"
}
```

### 내 글 조회

```txt
GET /api/me/posts
```

Auth: required

### 내 팔로잉 타임라인

```txt
GET /api/me/timeline?cursor={cursor}&size={size}
```

Auth: required

Response:

```json
{
  "content": [
    {
      "id": 10,
      "authorId": 2,
      "authorNickName": "철수",
      "authorProfileImageUrl": null,
      "content": "팔로우 피드 테스트",
      "imageUrls": [],
      "likeCount": 3,
      "createdAt": "...",
      "updatedAt": "..."
    }
  ],
  "nextCursor": 10,
  "hasNext": true
}
```

### 내가 좋아요한 글 조회

```txt
GET /api/me/likes
```

Auth: required

### 내 팔로잉 목록

```txt
GET /api/me/followings
```

Auth: required

### 내 팔로워 목록

```txt
GET /api/me/followers
```

Auth: required

### 프로필 이미지 업로드

```txt
POST /api/me/profile-image
```

Auth: required  
Content-Type: multipart/form-data

Form:

```txt
file=<image>
```

Response:

```json
{
  "profileImageUrl": "http://localhost:9000/mini-twitter/profile/1/uuid.png"
}
```

Rules:

```txt
허용 타입: image/jpeg, image/png, image/webp
최대 크기: 2MB
저장 경로: profile/{userId}/uuid.ext
```

## Post API

### 글 작성

```txt
POST /api/posts
```

Auth: required

Request:

```json
{
  "content": "오늘 Spring Security 정리했다."
}
```

Response:

```json
{
  "id": 1,
  "authorId": 1,
  "authorNickName": "민준",
  "authorProfileImageUrl": null,
  "content": "오늘 Spring Security 정리했다.",
  "imageUrls": [],
  "likeCount": 0,
  "createdAt": "...",
  "updatedAt": "..."
}
```

### 전체 글 목록 조회

```txt
GET /api/posts
```

### 특정 작성자 글 조회

```txt
GET /api/posts?authorId={userId}
```

### 전체 피드 조회

```txt
GET /api/posts/feed?cursor={cursor}&size={size}
```

Response:

```json
{
  "content": [],
  "nextCursor": null,
  "hasNext": false
}
```

### 팔로잉 타임라인 조회

```txt
GET /api/posts/timeline?userId={userId}&cursor={cursor}&size={size}
```

이 API는 공개 테스트용으로 유지할 수 있습니다.  
로그인 기반 화면에서는 `GET /api/me/timeline` 사용을 권장합니다.

### 글 단건 조회

```txt
GET /api/posts/{postId}
```

### 글 삭제

```txt
DELETE /api/posts/{postId}
```

Auth: required

Policy:

```txt
게시글 작성자만 삭제 가능
```

### 게시글 이미지 업로드

```txt
POST /api/posts/{postId}/images
```

Auth: required  
Content-Type: multipart/form-data

Form:

```txt
files=<image1>
files=<image2>
```

Response:

```json
{
  "postId": 1,
  "imageUrls": [
    "http://localhost:9000/mini-twitter/post/1/uuid-1.png",
    "http://localhost:9000/mini-twitter/post/1/uuid-2.png"
  ]
}
```

Rules:

```txt
게시글 작성자만 업로드 가능
게시글당 최대 4장
각 파일 최대 5MB
허용 타입: image/jpeg, image/png, image/webp
저장 경로: post/{postId}/uuid.ext
```

## Like API

### 좋아요 등록

```txt
POST /api/posts/{postId}/likes
```

Auth: required

Policy:

```txt
한 사용자는 같은 게시글에 한 번만 좋아요 가능
```

Response: PostResponse

### 좋아요 취소

```txt
DELETE /api/posts/{postId}/likes
```

Auth: required

Response: PostResponse

### 게시글 좋아요 유저 목록 조회

```txt
GET /api/posts/{postId}/likes/users
```

Auth: required

Policy:

```txt
게시글 작성자만 조회 가능
```

Response:

```json
[
  {
    "id": 2,
    "loginId": "chulsu",
    "nickName": "철수",
    "profileImageUrl": null
  }
]
```

## Follow API

### 팔로우

```txt
POST /api/users/{targetUserId}/follow
```

Auth: required

Policy:

```txt
자기 자신 팔로우 불가
중복 팔로우 불가
```

Response:

```json
{
  "id": 1,
  "followerId": 1,
  "followerNickName": "민준",
  "followingId": 2,
  "followingNickName": "철수",
  "createdAt": "..."
}
```

필드명은 실제 DTO 구현에 따라 `followerNickname` 또는 `followerNickName`일 수 있으니 프로젝트 코드와 맞춰야 합니다.

### 언팔로우

```txt
DELETE /api/users/{targetUserId}/follow
```

Auth: required

### 팔로우 상태 조회

```txt
GET /api/users/{targetUserId}/follow/status
```

Auth: required

Response:

```json
{
  "following": true
}
```

### 공개 팔로잉 목록 조회

```txt
GET /api/users/{userId}/followings
```

### 공개 팔로워 목록 조회

```txt
GET /api/users/{userId}/followers
```

Response:

```json
[
  {
    "userId": 2,
    "loginId": "chulsu",
    "nickName": "철수",
    "bio": "오늘도 점심 고민",
    "profileImageUrl": null
  }
]
```

## Status Codes

주요 상태 코드:

```txt
200 OK
201 Created
204 No Content
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
```

예시:

```txt
401: 인증 필요
403: 작성자 권한 없음
404: 사용자/게시글/좋아요 없음
409: 중복 loginId, 중복 nickName, 중복 팔로우, 중복 좋아요
```

## 삭제 또는 정리 후보 API

다음 API는 테스트 편의상 유지할 수 있지만, 최종적으로는 정리 대상입니다.

```txt
GET /api/users/by-nickname/{nickName}
GET /api/posts/timeline?userId={userId}
```

다음 API는 실제 기능 API가 완성되었으므로 삭제하는 것이 좋습니다.

```txt
POST /api/storage-test/images
```
