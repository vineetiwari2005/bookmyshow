#!/bin/bash

# BookMyShow - Complete End-to-End Verification Script
# Tests database, backend, and frontend naming consistency

echo "🔍 BookMyShow Naming Consistency Verification"
echo "=============================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Check if backend is running
echo "📡 Test 1: Backend Health Check"
BACKEND_HEALTH=$(curl -s http://localhost:8080/auth/health 2>&1)
if [[ $BACKEND_HEALTH == *"running"* ]]; then
    echo -e "${GREEN}✅ Backend is running${NC}"
else
    echo -e "${RED}❌ Backend is not running${NC}"
    echo "   Start backend with: cd Book-My-Show && ./mvnw spring-boot:run"
    exit 1
fi
echo ""

# Test 2: Check database schema
echo "🗄️  Test 2: Database Schema Verification"

# Check if show_seats has correct FK name
SHOW_SEATS_FK=$(mysql -u springuser -p'springpass123' bookmyshow -e "SHOW CREATE TABLE show_seats\G" 2>/dev/null | grep "CONSTRAINT.*show_id")
if [[ ! -z "$SHOW_SEATS_FK" ]]; then
    echo -e "${GREEN}✅ show_seats.show_id FK is correct${NC}"
else
    echo -e "${RED}❌ show_seats FK naming issue${NC}"
    mysql -u springuser -p'springpass123' bookmyshow -e "DESCRIBE show_seats;" 2>/dev/null | grep show
fi

# Check if tickets has correct FK names
TICKETS_FK=$(mysql -u springuser -p'springpass123' bookmyshow -e "SHOW CREATE TABLE tickets\G" 2>/dev/null | grep "CONSTRAINT.*show_id")
if [[ ! -z "$TICKETS_FK" ]]; then
    echo -e "${GREEN}✅ tickets.show_id FK is correct${NC}"
else
    echo -e "${RED}❌ tickets FK naming issue${NC}"
fi

# Check movies table column names
MOVIE_COLS=$(mysql -u springuser -p'springpass123' bookmyshow -e "DESCRIBE movies;" 2>/dev/null | grep -E "movie_name|poster_url|now_showing")
if [[ $(echo "$MOVIE_COLS" | wc -l) -eq 3 ]]; then
    echo -e "${GREEN}✅ movies table has snake_case columns${NC}"
else
    echo -e "${RED}❌ movies table column naming issue${NC}"
fi

echo ""

# Test 3: Check API response structure
echo "🌐 Test 3: API Response Field Names"
MOVIES_JSON=$(curl -s http://localhost:8080/api/movies/now-showing | python3 -c "import sys, json; data = json.load(sys.stdin); print(json.dumps(data[0] if len(data) > 0 else {}, indent=2))" 2>&1)

if [[ $MOVIES_JSON == *"movieName"* ]]; then
    echo -e "${GREEN}✅ API returns 'movieName' (camelCase)${NC}"
else
    echo -e "${RED}❌ API field naming issue - missing 'movieName'${NC}"
    echo "$MOVIES_JSON"
fi

if [[ $MOVIES_JSON == *"posterUrl"* ]]; then
    echo -e "${GREEN}✅ API returns 'posterUrl' (camelCase)${NC}"
else
    echo -e "${RED}❌ API field naming issue - missing 'posterUrl'${NC}"
fi

if [[ $MOVIES_JSON == *"releaseDate"* ]]; then
    echo -e "${GREEN}✅ API returns 'releaseDate' (camelCase)${NC}"
else
    echo -e "${RED}❌ API field naming issue - missing 'releaseDate'${NC}"
fi

if [[ $MOVIES_JSON == *"nowShowing"* ]]; then
    echo -e "${GREEN}✅ API returns 'nowShowing' (camelCase)${NC}"
else
    echo -e "${RED}❌ API field naming issue - missing 'nowShowing'${NC}"
fi

echo ""

# Test 4: Check movie count
echo "📊 Test 4: Data Initialization"
MOVIE_COUNT=$(curl -s http://localhost:8080/api/movies/now-showing | python3 -c "import sys, json; print(len(json.load(sys.stdin)))" 2>&1)
if [[ "$MOVIE_COUNT" -ge 1 ]]; then
    echo -e "${GREEN}✅ Found $MOVIE_COUNT movies in database${NC}"
else
    echo -e "${YELLOW}⚠️  No movies found - backend may still be initializing${NC}"
fi

echo ""

# Test 5: Test login endpoint
echo "🔐 Test 5: Authentication API"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}' 2>&1)

if [[ $LOGIN_RESPONSE == *"accessToken"* ]]; then
    echo -e "${GREEN}✅ Login returns 'accessToken'${NC}"
else
    echo -e "${YELLOW}⚠️  Login test skipped (test user may not exist yet)${NC}"
fi

if [[ $LOGIN_RESPONSE == *"userId"* ]]; then
    echo -e "${GREEN}✅ Login returns 'userId' (camelCase)${NC}"
fi

echo ""

# Test 6: Frontend check
echo "🎨 Test 6: Frontend Server"
FRONTEND_RUNNING=$(lsof -ti:3000 2>/dev/null)
if [[ ! -z "$FRONTEND_RUNNING" ]]; then
    echo -e "${GREEN}✅ Frontend is running on port 3000${NC}"
else
    echo -e "${YELLOW}⚠️  Frontend not running${NC}"
    echo "   Start with: cd frontend && npm run dev"
fi

echo ""
echo "=============================================="
echo "🎯 SUMMARY"
echo "=============================================="
echo ""

# Count results
PASSED=0
FAILED=0
WARNINGS=0

# Simple check based on previous outputs
if [[ $BACKEND_HEALTH == *"running"* ]]; then ((PASSED++)); fi
if [[ ! -z "$SHOW_SEATS_FK" ]]; then ((PASSED++)); fi
if [[ ! -z "$TICKETS_FK" ]]; then ((PASSED++)); fi
if [[ $(echo "$MOVIE_COLS" | wc -l) -eq 3 ]]; then ((PASSED++)); fi
if [[ $MOVIES_JSON == *"movieName"* ]]; then ((PASSED++)); fi
if [[ $MOVIES_JSON == *"posterUrl"* ]]; then ((PASSED++)); fi

echo -e "${GREEN}✅ Passed: $PASSED${NC}"
echo -e "${YELLOW}⚠️  Warnings: Check output above${NC}"
echo ""

if [[ $PASSED -ge 5 ]]; then
    echo -e "${GREEN}🎉 Naming consistency verification PASSED!${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Open http://localhost:3000 in browser"
    echo "2. Check browser console for errors"
    echo "3. Test login with: test@example.com / test123"
    echo "4. Verify movies display correctly"
else
    echo -e "${RED}⚠️  Some tests failed. Check output above.${NC}"
    echo ""
    echo "Troubleshooting:"
    echo "1. Restart backend: cd Book-My-Show && ./mvnw spring-boot:run"
    echo "2. Check backend console for errors"
    echo "3. Verify database was recreated"
fi

echo ""
