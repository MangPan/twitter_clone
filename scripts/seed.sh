#!/usr/bin/env bash

set -e

BASE_URL="http://localhost:8080"

echo "=================================="
echo " Mini Twitter Seed Data"
echo " BASE_URL=$BASE_URL"
echo "=================================="

echo ""
echo "1. Health check..."
curl -s "$BASE_URL/health" > /dev/null
echo "OK"

echo ""
echo "2. Create users..."

curl -s -X POST "$BASE_URL/api/users" \
  -H "Content-Type: application/json" \
  -d '{"nickName":"minjun","bio":"백엔드 하는 민준"}' > /dev/null

curl -s -X POST "$BASE_URL/api/users" \
  -H "Content-Type: application/json" \
  -d '{"nickName":"chulsu","bio":"철수입니다"}' > /dev/null

curl -s -X POST "$BASE_URL/api/users" \
  -H "Content-Type: application/json" \
  -d '{"nickName":"younghee","bio":"영희입니다"}' > /dev/null

curl -s -X POST "$BASE_URL/api/users" \
  -H "Content-Type: application/json" \
  -d '{"nickName":"backendcat","bio":"서버 고양이"}' > /dev/null

curl -s -X POST "$BASE_URL/api/users" \
  -H "Content-Type: application/json" \
  -d '{"nickName":"frontenddog","bio":"프론트 강아지"}' > /dev/null

echo "Users created."

echo ""
echo "3. Create posts..."

curl -s -X POST "$BASE_URL/api/posts" \
  -H "Content-Type: application/json" \
  -d '{"authorId":1,"content":"첫 글. Spring Boot 다시 만지는 중."}' > /dev/null

curl -s -X POST "$BASE_URL/api/posts" \
  -H "Content-Type: application/json" \
  -d '{"authorId":2,"content":"오늘 점심 뭐 먹지?"}' > /dev/null

curl -s -X POST "$BASE_URL/api/posts" \
  -H "Content-Type: application/json" \
  -d '{"authorId":3,"content":"미니 트위터 생각보다 재밌네."}' > /dev/null

curl -s -X POST "$BASE_URL/api/posts" \
  -H "Content-Type: application/json" \
  -d '{"authorId":4,"content":"JPA fetch join은 봐도 봐도 중요하다."}' > /dev/null

curl -s -X POST "$BASE_URL/api/posts" \
  -H "Content-Type: application/json" \
  -d '{"authorId":5,"content":"React에서 API 붙이는 중."}' > /dev/null

curl -s -X POST "$BASE_URL/api/posts" \
  -H "Content-Type: application/json" \
  -d '{"authorId":1,"content":"cursor 기반 피드까지 붙였다."}' > /dev/null

curl -s -X POST "$BASE_URL/api/posts" \
  -H "Content-Type: application/json" \
  -d '{"authorId":2,"content":"좋아요 기능 누가 눌렀냐 ㅋㅋ"}' > /dev/null

curl -s -X POST "$BASE_URL/api/posts" \
  -H "Content-Type: application/json" \
  -d '{"authorId":3,"content":"팔로우 피드가 생기니까 SNS 느낌 난다."}' > /dev/null

curl -s -X POST "$BASE_URL/api/posts" \
  -H "Content-Type: application/json" \
  -d '{"authorId":4,"content":"H2 메모리 DB라 재시작하면 싹 날아감."}' > /dev/null

curl -s -X POST "$BASE_URL/api/posts" \
  -H "Content-Type: application/json" \
  -d '{"authorId":5,"content":"그래서 seed script가 필요하다."}' > /dev/null

echo "Posts created."

echo ""
echo "4. Create follow relationships..."

# minjun follows chulsu, younghee, backendcat
curl -s -X POST "$BASE_URL/api/users/1/followings/2" > /dev/null
curl -s -X POST "$BASE_URL/api/users/1/followings/3" > /dev/null
curl -s -X POST "$BASE_URL/api/users/1/followings/4" > /dev/null

# chulsu follows minjun, frontenddog
curl -s -X POST "$BASE_URL/api/users/2/followings/1" > /dev/null
curl -s -X POST "$BASE_URL/api/users/2/followings/5" > /dev/null

# younghee follows minjun
curl -s -X POST "$BASE_URL/api/users/3/followings/1" > /dev/null

# backendcat follows frontenddog
curl -s -X POST "$BASE_URL/api/users/4/followings/5" > /dev/null

# frontenddog follows backendcat, minjun
curl -s -X POST "$BASE_URL/api/users/5/followings/4" > /dev/null
curl -s -X POST "$BASE_URL/api/users/5/followings/1" > /dev/null

echo "Follows created."

echo ""
echo "5. Add likes..."

curl -s -X POST "$BASE_URL/api/posts/1/like" > /dev/null
curl -s -X POST "$BASE_URL/api/posts/1/like" > /dev/null
curl -s -X POST "$BASE_URL/api/posts/3/like" > /dev/null
curl -s -X POST "$BASE_URL/api/posts/4/like" > /dev/null
curl -s -X POST "$BASE_URL/api/posts/4/like" > /dev/null
curl -s -X POST "$BASE_URL/api/posts/5/like" > /dev/null
curl -s -X POST "$BASE_URL/api/posts/8/like" > /dev/null

echo "Likes added."

echo ""
echo "=================================="
echo " Seed completed!"
echo "=================================="

echo ""
echo "Users:"
curl -s "$BASE_URL/api/users"

echo ""
echo ""
echo "Feed:"
curl -s "$BASE_URL/api/posts/feed?size=5"

echo ""
echo ""
echo "minjun timeline:"
curl -s "$BASE_URL/api/posts/timeline?userId=1&size=5"

echo ""
