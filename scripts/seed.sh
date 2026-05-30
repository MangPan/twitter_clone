#!/usr/bin/env bash

set -e

BASE_URL="${BASE_URL:-http://localhost:8080}"
SEED_DIR="/tmp/mini-twitter-seed-images"

echo "=================================="
echo " Mini Twitter JWT + Image Seed Data"
echo " BASE_URL=$BASE_URL"
echo " SEED_DIR=$SEED_DIR"
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

prepare_images() {
  echo ""
  echo "2. Prepare seed images..."

  mkdir -p "$SEED_DIR"

  if [ ! -f "/tmp/test.png" ]; then
    echo "ERROR: /tmp/test.png not found."
    echo "Prepare test image first:"
    echo "  cp your-image.png /tmp/test.png"
    exit 1
  fi

  cp /tmp/test.png "$SEED_DIR/red.png"
  cp /tmp/test.png "$SEED_DIR/blue.png"
  cp /tmp/test.png "$SEED_DIR/green.png"
  cp /tmp/test.png "$SEED_DIR/yellow.png"

  echo "Images prepared:"
  ls -lh "$SEED_DIR"/*.png
}

create_user() {
  local login_id="$1"
  local password="$2"
  local nick_name="$3"
  local bio="$4"

  echo "Create user: $login_id / $nick_name"

  local response
  local status
  local body

  response=$(curl -sS -w "\n%{http_code}" -X POST "$BASE_URL/api/users" \
    -H "Content-Type: application/json" \
    -d "$(jq -n \
      --arg loginId "$login_id" \
      --arg password "$password" \
      --arg nickName "$nick_name" \
      --arg bio "$bio" \
      '{loginId: $loginId, password: $password, nickName: $nickName, bio: $bio}')")

  status=$(echo "$response" | tail -n1)
  body=$(echo "$response" | sed '$d')

  if [ "$status" != "201" ]; then
    echo ""
    echo "ERROR: create_user failed"
    echo "status=$status"
    echo "loginId=$login_id"
    echo "response=$body"
    exit 1
  fi
}

login_user() {
  local login_id="$1"
  local password="$2"

  local response
  local status
  local body
  local token

  echo "Login user: $login_id" >&2

  response=$(curl -sS -w "\n%{http_code}" -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "$(jq -n \
      --arg loginId "$login_id" \
      --arg password "$password" \
      '{loginId: $loginId, password: $password}')")

  status=$(printf '%s\n' "$response" | tail -n1)
  body=$(printf '%s\n' "$response" | sed '$d')

  if [ "$status" != "200" ]; then
    echo "ERROR: login failed" >&2
    echo "status=$status" >&2
    echo "loginId=$login_id" >&2
    echo "response=$body" >&2
    exit 1
  fi

  token=$(printf '%s' "$body" | jq -r '.accessToken')

  # remove CR/LF and possible invisible control chars
  token=$(printf '%s' "$token" | tr -d '\r\n' | sed $'s/\x1b\\[[0-9;]*[A-Za-z]//g')

  if ! printf '%s' "$token" | grep -Eq '^[A-Za-z0-9._-]+$'; then
    echo "ERROR: token contains invalid characters" >&2
    echo "loginId=$login_id" >&2
    printf '%s' "$token" | od -An -tx1 >&2
    exit 1
  fi

  printf '%s' "$token"
}

validate_token() {
  local name="$1"
  local token="$2"

  if [ -z "$token" ]; then
    echo "ERROR: $name token is empty" >&2
    exit 1
  fi

  if ! printf '%s' "$token" | grep -Eq '^[A-Za-z0-9._-]+$'; then
    echo "ERROR: $name token is invalid" >&2
    printf '%s' "$token" | od -An -tx1 >&2
    exit 1
  fi
}

upload_profile_image() {
  local token="$1"
  local image_path="$2"

  local response
  local status
  local body

  if ! printf '%s' "$token" | grep -Eq '^[A-Za-z0-9._-]+$'; then
    echo "ERROR: invalid token passed to upload_profile_image" >&2
    printf '%s' "$token" | od -An -tx1 >&2
    exit 1
  fi

  response=$(curl -sS -w "\n%{http_code}" -X POST "$BASE_URL/api/me/profile-image" \
    -H "Authorization: Bearer $token" \
    -F "file=@$image_path")

  status=$(printf '%s\n' "$response" | tail -n1)
  body=$(printf '%s\n' "$response" | sed '$d')

  if [ "$status" != "200" ]; then
    echo "" >&2
    echo "ERROR: upload_profile_image failed" >&2
    echo "status=$status" >&2
    echo "image=$image_path" >&2
    echo "response=$body" >&2
    exit 1
  fi
}

create_post() {
  local token="$1"
  local content="$2"

  local response
  local status
  local body
  local post_id

  if [ -z "$content" ]; then
    echo "ERROR: create_post content is empty" >&2
    exit 1
  fi

  if ! printf '%s' "$token" | grep -Eq '^[A-Za-z0-9._-]+$'; then
    echo "ERROR: invalid token passed to create_post" >&2
    printf '%s' "$token" | od -An -tx1 >&2
    exit 1
  fi

  response=$(curl -sS -w "\n%{http_code}" -X POST "$BASE_URL/api/posts" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $token" \
    -d "$(jq -n --arg content "$content" '{content: $content}')")

  status=$(printf '%s\n' "$response" | tail -n1)
  body=$(printf '%s\n' "$response" | sed '$d')

  if [ "$status" != "201" ]; then
    echo "" >&2
    echo "ERROR: create_post failed" >&2
    echo "status=$status" >&2
    echo "content=$content" >&2
    echo "response=$body" >&2
    exit 1
  fi

  post_id=$(printf '%s' "$body" | jq -r '.id')

  if [ -z "$post_id" ] || [ "$post_id" = "null" ]; then
    echo "ERROR: post id is empty" >&2
    echo "response=$body" >&2
    exit 1
  fi

  printf '%s' "$post_id"
}

upload_post_images() {
  local token="$1"
  local post_id="$2"
  shift 2

  local curl_args=()

  for image_path in "$@"; do
    curl_args+=(-F "files=@$image_path")
  done

  curl -s -X POST "$BASE_URL/api/posts/$post_id/images" \
    -H "Authorization: Bearer $token" \
    "${curl_args[@]}" > /dev/null
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
prepare_images

echo ""
echo "3. Create users..."

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
echo "4. Login users..."

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
validate_token "MINJUN" "$MINJUN_TOKEN"
validate_token "CHULSU" "$CHULSU_TOKEN"
validate_token "YOUNGHEE" "$YOUNGHEE_TOKEN"
validate_token "BACKENDCAT" "$BACKENDCAT_TOKEN"
validate_token "FRONTENDDOG" "$FRONTENDDOG_TOKEN"
validate_token "SPRINGMAN" "$SPRINGMAN_TOKEN"
validate_token "DBMASTER" "$DBMASTER_TOKEN"
validate_token "DEPLOYKING" "$DEPLOYKING_TOKEN"
validate_token "UXRABBIT" "$UXRABBIT_TOKEN"
validate_token "TESTBEAR" "$TESTBEAR_TOKEN"
echo "Tokens issued."

echo ""
echo "5. Upload profile images..."

upload_profile_image "$MINJUN_TOKEN" "$SEED_DIR/red.png"
upload_profile_image "$CHULSU_TOKEN" "$SEED_DIR/blue.png"
upload_profile_image "$YOUNGHEE_TOKEN" "$SEED_DIR/green.png"
upload_profile_image "$BACKENDCAT_TOKEN" "$SEED_DIR/yellow.png"
upload_profile_image "$FRONTENDDOG_TOKEN" "$SEED_DIR/red.png"
upload_profile_image "$SPRINGMAN_TOKEN" "$SEED_DIR/blue.png"
upload_profile_image "$DBMASTER_TOKEN" "$SEED_DIR/green.png"
upload_profile_image "$DEPLOYKING_TOKEN" "$SEED_DIR/yellow.png"
upload_profile_image "$UXRABBIT_TOKEN" "$SEED_DIR/red.png"
upload_profile_image "$TESTBEAR_TOKEN" "$SEED_DIR/blue.png"

echo "Profile images uploaded."

echo ""
echo "6. Create posts..."

P1=$(create_post "$MINJUN_TOKEN" "JWT 기반으로 글 작성 성공. 이제 authorId 안 보낸다.")
P2=$(create_post "$MINJUN_TOKEN" "Spring Security 붙이니까 갑자기 백엔드 느낌 난다.")
P3=$(create_post "$MINJUN_TOKEN" "다음 목표는 MinIO로 이미지 업로드 연습.")

P4=$(create_post "$CHULSU_TOKEN" "오늘 점심 뭐 먹지?")
P5=$(create_post "$CHULSU_TOKEN" "좋아요 기능 관계 엔티티로 바뀐 거 마음에 든다.")
P6=$(create_post "$CHULSU_TOKEN" "팔로우 피드가 있으니까 진짜 SNS 같네.")

P7=$(create_post "$YOUNGHEE_TOKEN" "React 화면도 나쁘진 않은데 CSS가 어렵다.")
P8=$(create_post "$YOUNGHEE_TOKEN" "프로필 페이지에서 내 글 모아보는 거 좋음.")
P9=$(create_post "$YOUNGHEE_TOKEN" "좋아요 목록은 본인만 볼 수 있게 한 거 꽤 실전적.")

P10=$(create_post "$BACKENDCAT_TOKEN" "JPA LAZY는 기본, 필요할 때 fetch join.")
P11=$(create_post "$BACKENDCAT_TOKEN" "N+1은 작을 때 잡아야 나중에 덜 아프다.")
P12=$(create_post "$BACKENDCAT_TOKEN" "Service에서 권한 체크하는 구조가 깔끔하다.")

P13=$(create_post "$FRONTENDDOG_TOKEN" "API 응답 필드명 nickName인지 nickname인지 통일 좀 ㅋㅋ")
P14=$(create_post "$FRONTENDDOG_TOKEN" "프론트는 일단 돌아가면 반은 성공이다.")
P15=$(create_post "$FRONTENDDOG_TOKEN" "토큰 저장하고 Authorization 헤더 붙이는 흐름 확인 필요.")

P16=$(create_post "$SPRINGMAN_TOKEN" "SecurityConfig permitAll 순서 조심해야 함.")
P17=$(create_post "$SPRINGMAN_TOKEN" "JWT 필터는 UsernamePasswordAuthenticationFilter 전에 넣는다.")
P18=$(create_post "$SPRINGMAN_TOKEN" "Security 예외는 GlobalExceptionHandler로 안 감.")

P19=$(create_post "$DBMASTER_TOKEN" "좋아요를 count 컬럼으로 둘지 count 쿼리로 할지 고민됨.")
P20=$(create_post "$DBMASTER_TOKEN" "지금은 likeCount 캐시 컬럼 유지가 적당하다.")
P21=$(create_post "$DBMASTER_TOKEN" "나중에 PostgreSQL 붙이면 더 재밌겠다.")

P22=$(create_post "$DEPLOYKING_TOKEN" "H2 메모리라 재시작하면 데이터 날아감.")
P23=$(create_post "$DEPLOYKING_TOKEN" "그래서 seed.sh가 필수다.")
P24=$(create_post "$DEPLOYKING_TOKEN" "배포하려면 H2 file 또는 PostgreSQL로 가야 함.")

P25=$(create_post "$UXRABBIT_TOKEN" "버튼이 너무 작으면 아무도 안 누른다.")
P26=$(create_post "$UXRABBIT_TOKEN" "프로필 사진 들어가면 서비스 느낌 확 살 듯.")
P27=$(create_post "$UXRABBIT_TOKEN" "게시글 사진도 있으면 뻘글 올리기 좋아진다.")

P28=$(create_post "$TESTBEAR_TOKEN" "curl 테스트도 좋지만 결국 자동 테스트가 필요하다.")
P29=$(create_post "$TESTBEAR_TOKEN" "권한 테스트는 꼭 넣어야 한다.")
P30=$(create_post "$TESTBEAR_TOKEN" "작성자만 좋아요 누른 사람 목록 보는 거 테스트하자.")

echo "Posts created."

echo ""
echo "7. Upload post images..."

upload_post_images "$MINJUN_TOKEN" "$P1" "$SEED_DIR/red.png"
upload_post_images "$MINJUN_TOKEN" "$P3" "$SEED_DIR/red.png" "$SEED_DIR/blue.png"

upload_post_images "$CHULSU_TOKEN" "$P4" "$SEED_DIR/green.png"
upload_post_images "$CHULSU_TOKEN" "$P6" "$SEED_DIR/yellow.png" "$SEED_DIR/red.png"

upload_post_images "$YOUNGHEE_TOKEN" "$P7" "$SEED_DIR/blue.png"
upload_post_images "$YOUNGHEE_TOKEN" "$P9" "$SEED_DIR/green.png" "$SEED_DIR/yellow.png"

upload_post_images "$BACKENDCAT_TOKEN" "$P10" "$SEED_DIR/red.png"
upload_post_images "$FRONTENDDOG_TOKEN" "$P13" "$SEED_DIR/blue.png"
upload_post_images "$SPRINGMAN_TOKEN" "$P16" "$SEED_DIR/green.png"
upload_post_images "$DBMASTER_TOKEN" "$P19" "$SEED_DIR/yellow.png"
upload_post_images "$DEPLOYKING_TOKEN" "$P22" "$SEED_DIR/red.png"
upload_post_images "$UXRABBIT_TOKEN" "$P26" "$SEED_DIR/blue.png" "$SEED_DIR/green.png"
upload_post_images "$TESTBEAR_TOKEN" "$P30" "$SEED_DIR/yellow.png"

echo "Post images uploaded."

echo ""
echo "8. Create follow relationships..."

# user ids are expected to be 1~10 in a fresh H2 database.
follow_user "$MINJUN_TOKEN" 2
follow_user "$MINJUN_TOKEN" 3
follow_user "$MINJUN_TOKEN" 4
follow_user "$MINJUN_TOKEN" 6

follow_user "$CHULSU_TOKEN" 1
follow_user "$CHULSU_TOKEN" 3
follow_user "$CHULSU_TOKEN" 5
follow_user "$CHULSU_TOKEN" 9

follow_user "$YOUNGHEE_TOKEN" 1
follow_user "$YOUNGHEE_TOKEN" 5
follow_user "$YOUNGHEE_TOKEN" 9

follow_user "$BACKENDCAT_TOKEN" 1
follow_user "$BACKENDCAT_TOKEN" 6
follow_user "$BACKENDCAT_TOKEN" 7
follow_user "$BACKENDCAT_TOKEN" 10

follow_user "$FRONTENDDOG_TOKEN" 1
follow_user "$FRONTENDDOG_TOKEN" 3
follow_user "$FRONTENDDOG_TOKEN" 9

follow_user "$SPRINGMAN_TOKEN" 1
follow_user "$SPRINGMAN_TOKEN" 4
follow_user "$SPRINGMAN_TOKEN" 7

follow_user "$DBMASTER_TOKEN" 4
follow_user "$DBMASTER_TOKEN" 6
follow_user "$DBMASTER_TOKEN" 8

follow_user "$DEPLOYKING_TOKEN" 1
follow_user "$DEPLOYKING_TOKEN" 7
follow_user "$DEPLOYKING_TOKEN" 10

follow_user "$UXRABBIT_TOKEN" 3
follow_user "$UXRABBIT_TOKEN" 5
follow_user "$UXRABBIT_TOKEN" 10

follow_user "$TESTBEAR_TOKEN" 1
follow_user "$TESTBEAR_TOKEN" 4
follow_user "$TESTBEAR_TOKEN" 6
follow_user "$TESTBEAR_TOKEN" 7

echo "Follows created."

echo ""
echo "9. Add likes..."

like_post "$CHULSU_TOKEN" "$P1"
like_post "$YOUNGHEE_TOKEN" "$P1"
like_post "$BACKENDCAT_TOKEN" "$P1"
like_post "$TESTBEAR_TOKEN" "$P1"

like_post "$MINJUN_TOKEN" "$P4"
like_post "$YOUNGHEE_TOKEN" "$P4"
like_post "$FRONTENDDOG_TOKEN" "$P4"
like_post "$UXRABBIT_TOKEN" "$P4"

like_post "$MINJUN_TOKEN" "$P7"
like_post "$CHULSU_TOKEN" "$P7"
like_post "$FRONTENDDOG_TOKEN" "$P7"
like_post "$SPRINGMAN_TOKEN" "$P7"

like_post "$MINJUN_TOKEN" "$P10"
like_post "$SPRINGMAN_TOKEN" "$P10"
like_post "$DBMASTER_TOKEN" "$P10"
like_post "$TESTBEAR_TOKEN" "$P10"

like_post "$FRONTENDDOG_TOKEN" "$P16"
like_post "$DBMASTER_TOKEN" "$P16"
like_post "$DEPLOYKING_TOKEN" "$P16"

like_post "$MINJUN_TOKEN" "$P19"
like_post "$BACKENDCAT_TOKEN" "$P19"
like_post "$SPRINGMAN_TOKEN" "$P19"
like_post "$TESTBEAR_TOKEN" "$P19"

like_post "$MINJUN_TOKEN" "$P22"
like_post "$CHULSU_TOKEN" "$P22"
like_post "$DBMASTER_TOKEN" "$P22"
like_post "$UXRABBIT_TOKEN" "$P22"

like_post "$CHULSU_TOKEN" "$P25"
like_post "$YOUNGHEE_TOKEN" "$P25"
like_post "$FRONTENDDOG_TOKEN" "$P25"
like_post "$TESTBEAR_TOKEN" "$P25"

like_post "$MINJUN_TOKEN" "$P28"
like_post "$BACKENDCAT_TOKEN" "$P28"
like_post "$DEPLOYKING_TOKEN" "$P28"
like_post "$UXRABBIT_TOKEN" "$P28"

like_post "$CHULSU_TOKEN" "$P30"
like_post "$SPRINGMAN_TOKEN" "$P30"
like_post "$DBMASTER_TOKEN" "$P30"
like_post "$DEPLOYKING_TOKEN" "$P30"
like_post "$UXRABBIT_TOKEN" "$P30"

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
print_json "post $P1 like users as author minjun" "$BASE_URL/api/posts/$P1/likes/users" "$MINJUN_TOKEN"

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
