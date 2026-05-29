#!/usr/bin/env bash

set -e

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "=================================="
echo " Mini Twitter JWT Seed Data"
echo " BASE_URL=$BASE_URL"
echo "=================================="

health_check() {
  echo ""
  echo "1. Health check..."

  if ! curl -s "$BASE_URL/health" > /dev/null; then
    echo "ERROR: Backend server is not running."
    echo "Start backend first:"
    echo "  ./gradlew bootRun"
    exit 1
  fi

  echo "OK"
}

create_user() {
  local login_id="$1"
  local password="$2"
  local nick_name="$3"
  local bio="$4"

  echo "Create user: $login_id / $nick_name"

  curl -s -X POST "$BASE_URL/api/users" \
    -H "Content-Type: application/json" \
    -d "{
      \"loginId\": \"$login_id\",
      \"password\": \"$password\",
      \"nickName\": \"$nick_name\",
      \"bio\": \"$bio\"
    }" > /dev/null
}

login_user() {
  local login_id="$1"
  local password="$2"

  curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{
      \"loginId\": \"$login_id\",
      \"password\": \"$password\"
    }" | jq -r '.accessToken'
}

create_post() {
  local token="$1"
  local content="$2"

  curl -s -X POST "$BASE_URL/api/posts" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $token" \
    -d "{
      \"content\": \"$content\"
    }" > /dev/null
}

follow_user() {
  local token="$1"
  local target_user_id="$2"

  curl -s -X POST "$BASE_URL/api/users/$target_user_id/follow" \
    -H "Authorization: Bearer $token" > /dev/null
}

like_post() {
  local token="$1"
  local post_id="$2"

  curl -s -X POST "$BASE_URL/api/posts/$post_id/likes" \
    -H "Authorization: Bearer $token" > /dev/null
}

print_json() {
  local title="$1"
  local url="$2"
  local token="${3:-}"

  echo ""
  echo "---- $title ----"

  if [ -n "$token" ]; then
    curl -s "$url" \
      -H "Authorization: Bearer $token" | jq
  else
    curl -s "$url" | jq
  fi
}

health_check

echo ""
echo "2. Create users..."

create_user "minjun" "password1234" "민준" "백엔드 하는 중"
create_user "chulsu" "password1234" "철수" "오늘도 점심 고민"
create_user "younghee" "password1234" "영희" "프론트엔드 관심 있음"
create_user "backendcat" "password1234" "백엔드고양이" "JPA fetch join 좋아함"
create_user "frontenddog" "password1234" "프론트강아지" "CSS랑 싸우는 중"
create_user "springman" "password1234" "스프링맨" "스프링 부트 실험실"
create_user "dbmaster" "password1234" "디비장인" "쿼리 튜닝은 어렵다"
create_user "deployking" "password1234" "배포왕" "배포는 항상 금요일에 터짐"
create_user "uxrabbit" "password1234" "UX토끼" "버튼 위치에 예민함"
create_user "testbear" "password1234" "테스트곰" "테스트 없으면 불안함"

echo ""
echo "3. Login users..."

MINJUN_TOKEN=$(login_user "minjun" "password1234")
CHULSU_TOKEN=$(login_user "chulsu" "password1234")
YOUNGHEE_TOKEN=$(login_user "younghee" "password1234")
BACKENDCAT_TOKEN=$(login_user "backendcat" "password1234")
FRONTENDDOG_TOKEN=$(login_user "frontenddog" "password1234")
SPRINGMAN_TOKEN=$(login_user "springman" "password1234")
DBMASTER_TOKEN=$(login_user "dbmaster" "password1234")
DEPLOYKING_TOKEN=$(login_user "deployking" "password1234")
UXRABBIT_TOKEN=$(login_user "uxrabbit" "password1234")
TESTBEAR_TOKEN=$(login_user "testbear" "password1234")

echo "Tokens issued."

echo ""
echo "4. Create posts..."

create_post "$MINJUN_TOKEN" "JWT 기반으로 글 작성 성공. 이제 authorId 안 보낸다."
create_post "$MINJUN_TOKEN" "Spring Security 붙이니까 갑자기 백엔드 느낌 난다."
create_post "$MINJUN_TOKEN" "다음 목표는 MinIO로 이미지 업로드 연습."

create_post "$CHULSU_TOKEN" "오늘 점심 뭐 먹지?"
create_post "$CHULSU_TOKEN" "좋아요 기능 관계 엔티티로 바뀐 거 마음에 든다."
create_post "$CHULSU_TOKEN" "팔로우 피드가 있으니까 진짜 SNS 같네."

create_post "$YOUNGHEE_TOKEN" "React 화면도 나쁘진 않은데 CSS가 어렵다."
create_post "$YOUNGHEE_TOKEN" "프로필 페이지에서 내 글 모아보는 거 좋음."
create_post "$YOUNGHEE_TOKEN" "좋아요 목록은 본인만 볼 수 있게 한 거 꽤 실전적."

create_post "$BACKENDCAT_TOKEN" "JPA LAZY는 기본, 필요할 때 fetch join."
create_post "$BACKENDCAT_TOKEN" "N+1은 작을 때 잡아야 나중에 덜 아프다."
create_post "$BACKENDCAT_TOKEN" "Service에서 권한 체크하는 구조가 깔끔하다."

create_post "$FRONTENDDOG_TOKEN" "API 응답 필드명 nickName인지 nickname인지 통일 좀 ㅋㅋ"
create_post "$FRONTENDDOG_TOKEN" "프론트는 일단 돌아가면 반은 성공이다."
create_post "$FRONTENDDOG_TOKEN" "토큰 저장하고 Authorization 헤더 붙이는 흐름 확인 필요."

create_post "$SPRINGMAN_TOKEN" "SecurityConfig permitAll 순서 조심해야 함."
create_post "$SPRINGMAN_TOKEN" "JWT 필터는 UsernamePasswordAuthenticationFilter 전에 넣는다."
create_post "$SPRINGMAN_TOKEN" "Security 예외는 GlobalExceptionHandler로 안 감."

create_post "$DBMASTER_TOKEN" "좋아요를 count 컬럼으로 둘지 count 쿼리로 할지 고민됨."
create_post "$DBMASTER_TOKEN" "지금은 likeCount 캐시 컬럼 유지가 적당하다."
create_post "$DBMASTER_TOKEN" "나중에 PostgreSQL 붙이면 더 재밌겠다."

create_post "$DEPLOYKING_TOKEN" "H2 메모리라 재시작하면 데이터 날아감."
create_post "$DEPLOYKING_TOKEN" "그래서 seed.sh가 필수다."
create_post "$DEPLOYKING_TOKEN" "배포하려면 H2 file 또는 PostgreSQL로 가야 함."

create_post "$UXRABBIT_TOKEN" "버튼이 너무 작으면 아무도 안 누른다."
create_post "$UXRABBIT_TOKEN" "프로필 사진 들어가면 서비스 느낌 확 살 듯."
create_post "$UXRABBIT_TOKEN" "게시글 사진도 있으면 뻘글 올리기 좋아진다."

create_post "$TESTBEAR_TOKEN" "curl 테스트도 좋지만 결국 자동 테스트가 필요하다."
create_post "$TESTBEAR_TOKEN" "권한 테스트는 꼭 넣어야 한다."
create_post "$TESTBEAR_TOKEN" "작성자만 좋아요 누른 사람 목록 보는 거 테스트하자."

echo "Posts created."

echo ""
echo "5. Create follow relationships..."

# user ids are expected to be 1~10 in a fresh H2 database.
# 1 minjun follows 2,3,4,6
follow_user "$MINJUN_TOKEN" 2
follow_user "$MINJUN_TOKEN" 3
follow_user "$MINJUN_TOKEN" 4
follow_user "$MINJUN_TOKEN" 6

# 2 chulsu follows 1,3,5,9
follow_user "$CHULSU_TOKEN" 1
follow_user "$CHULSU_TOKEN" 3
follow_user "$CHULSU_TOKEN" 5
follow_user "$CHULSU_TOKEN" 9

# 3 younghee follows 1,5,9
follow_user "$YOUNGHEE_TOKEN" 1
follow_user "$YOUNGHEE_TOKEN" 5
follow_user "$YOUNGHEE_TOKEN" 9

# 4 backendcat follows 1,6,7,10
follow_user "$BACKENDCAT_TOKEN" 1
follow_user "$BACKENDCAT_TOKEN" 6
follow_user "$BACKENDCAT_TOKEN" 7
follow_user "$BACKENDCAT_TOKEN" 10

# 5 frontenddog follows 1,3,9
follow_user "$FRONTENDDOG_TOKEN" 1
follow_user "$FRONTENDDOG_TOKEN" 3
follow_user "$FRONTENDDOG_TOKEN" 9

# 6 springman follows 1,4,7
follow_user "$SPRINGMAN_TOKEN" 1
follow_user "$SPRINGMAN_TOKEN" 4
follow_user "$SPRINGMAN_TOKEN" 7

# 7 dbmaster follows 4,6,8
follow_user "$DBMASTER_TOKEN" 4
follow_user "$DBMASTER_TOKEN" 6
follow_user "$DBMASTER_TOKEN" 8

# 8 deployking follows 1,7,10
follow_user "$DEPLOYKING_TOKEN" 1
follow_user "$DEPLOYKING_TOKEN" 7
follow_user "$DEPLOYKING_TOKEN" 10

# 9 uxrabbit follows 3,5,10
follow_user "$UXRABBIT_TOKEN" 3
follow_user "$UXRABBIT_TOKEN" 5
follow_user "$UXRABBIT_TOKEN" 10

# 10 testbear follows 1,4,6,7
follow_user "$TESTBEAR_TOKEN" 1
follow_user "$TESTBEAR_TOKEN" 4
follow_user "$TESTBEAR_TOKEN" 6
follow_user "$TESTBEAR_TOKEN" 7

echo "Follows created."

echo ""
echo "6. Add likes..."

# like post ids assume fresh DB and 30 posts created above.
like_post "$CHULSU_TOKEN" 1
like_post "$YOUNGHEE_TOKEN" 1
like_post "$BACKENDCAT_TOKEN" 1
like_post "$TESTBEAR_TOKEN" 1

like_post "$MINJUN_TOKEN" 4
like_post "$YOUNGHEE_TOKEN" 4
like_post "$FRONTENDDOG_TOKEN" 4
like_post "$UXRABBIT_TOKEN" 4

like_post "$MINJUN_TOKEN" 7
like_post "$CHULSU_TOKEN" 7
like_post "$FRONTENDDOG_TOKEN" 7
like_post "$SPRINGMAN_TOKEN" 7

like_post "$MINJUN_TOKEN" 10
like_post "$SPRINGMAN_TOKEN" 10
like_post "$DBMASTER_TOKEN" 10
like_post "$TESTBEAR_TOKEN" 10

like_post "$FRONTENDDOG_TOKEN" 16
like_post "$DBMASTER_TOKEN" 16
like_post "$DEPLOYKING_TOKEN" 16

like_post "$MINJUN_TOKEN" 19
like_post "$BACKENDCAT_TOKEN" 19
like_post "$SPRINGMAN_TOKEN" 19
like_post "$TESTBEAR_TOKEN" 19

like_post "$MINJUN_TOKEN" 22
like_post "$CHULSU_TOKEN" 22
like_post "$DBMASTER_TOKEN" 22
like_post "$UXRABBIT_TOKEN" 22

like_post "$CHULSU_TOKEN" 25
like_post "$YOUNGHEE_TOKEN" 25
like_post "$FRONTENDDOG_TOKEN" 25
like_post "$TESTBEAR_TOKEN" 25

like_post "$MINJUN_TOKEN" 28
like_post "$BACKENDCAT_TOKEN" 28
like_post "$DEPLOYKING_TOKEN" 28
like_post "$UXRABBIT_TOKEN" 28

like_post "$CHULSU_TOKEN" 30
like_post "$SPRINGMAN_TOKEN" 30
like_post "$DBMASTER_TOKEN" 30
like_post "$DEPLOYKING_TOKEN" 30
like_post "$UXRABBIT_TOKEN" 30

echo "Likes added."

echo ""
echo "=================================="
echo " Seed completed!"
echo "=================================="

print_json "Users" "$BASE_URL/api/users"
print_json "Public feed size=5" "$BASE_URL/api/posts/feed?size=5"
print_json "minjun /api/me" "$BASE_URL/api/me" "$MINJUN_TOKEN"
print_json "minjun /api/me/posts" "$BASE_URL/api/me/posts" "$MINJUN_TOKEN"
print_json "minjun /api/me/timeline size=5" "$BASE_URL/api/me/timeline?size=5" "$MINJUN_TOKEN"
print_json "chulsu /api/me/likes" "$BASE_URL/api/me/likes" "$CHULSU_TOKEN"
print_json "post 1 like users as author minjun" "$BASE_URL/api/posts/1/likes/users" "$MINJUN_TOKEN"

echo ""
echo "Sample login accounts:"
echo "  minjun / password1234"
echo "  chulsu / password1234"
echo "  younghee / password1234"
echo "  backendcat / password1234"
echo "  frontenddog / password1234"
echo "  springman / password1234"
echo "  dbmaster / password1234"
echo "  deployking / password1234"
echo "  uxrabbit / password1234"
echo "  testbear / password1234"
echo ""
