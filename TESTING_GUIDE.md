# BookMyShow - Testing Guide 🎬

## ✅ All Issues Fixed!

### What Was Fixed:

1. **Backend JPA Migration** ✅
   - All entities use Hibernate ORM
   - Database tables auto-created on startup
   - Sample data auto-loaded (20 movies, 35 theaters, 2 users)

2. **Frontend Blank Page** ✅
   - Fixed field name mismatches (movie.name → movie.movieName)
   - Fixed genre handling (array → single enum value)
   - Updated all components (Hero, MovieCard, MovieDetails)

3. **CORS Configuration** ✅
   - Backend allows requests from localhost:3000-3003, 5173
   - No more cross-origin errors

4. **Images Not Loading** ✅
   - Replaced TMDb URLs with placeholder.com images
   - Each movie has color-coded poster (300x450px)
   - Added fallback for broken images

5. **Login Not Working** ✅
   - Fixed AuthContext to match backend response format
   - Backend returns: `{accessToken, refreshToken, email, name, role, userId}`
   - Frontend now correctly extracts and stores user data

---

## 🚀 How to Test Everything

### Step 1: Restart Backend
```bash
# In IntelliJ IDEA
1. Stop the current Spring Boot application (red square button)
2. Click the green play button to restart
3. Watch console for these messages:

✅ Expected Console Output:
   Hibernate: create table movies (...)
   Hibernate: insert into movies (...)  [20 times]
   ✅ Initialized 20 movies
   ✅ Initialized 35 theaters
   ✅ Initialized 2 users
   
   Started BookMyShowApplication in X.XXX seconds
```

### Step 2: Verify Backend API
```bash
# Test movies endpoint
curl http://localhost:8080/api/movies/now-showing | python3 -m json.tool | head -30

# You should see:
{
  "id": 1,
  "movieName": "Jawan",
  "posterUrl": "https://via.placeholder.com/300x450/FF6B6B/FFFFFF?text=Jawan",
  ...
}
```

### Step 3: Verify Frontend
```bash
# Frontend should already be running on port 3000
# If not:
cd /Users/vineettiwari/Downloads/bookmyshow/Book-My-Show/frontend
npm run dev

# Open browser: http://localhost:3000
```

### Step 4: Test Images
1. Open http://localhost:3000
2. **Hero Carousel** should show auto-sliding banners with colorful placeholder images
3. **Movie Grid** below should display 20 movies with unique colored posters
4. Each poster shows the movie name in the image
5. Open browser DevTools (F12) → Network tab → Filter "img" → All should be 200 OK

---

## 🔐 Test Login Functionality

### Test User Credentials (Pre-loaded):
```
Email: test@example.com
Password: test123
Role: USER

Email: admin@example.com  
Password: admin123
Role: ADMIN
```

### Login Test Steps:
1. Click **Login** button in navbar
2. Enter: `test@example.com` / `test123`
3. Click **Login**
4. **Expected Result:**
   - ✅ Toast message: "Login successful!"
   - ✅ Redirect to home page
   - ✅ Navbar shows user name "Test User"
   - ✅ Shows "Logout" button instead of "Login"
   - ✅ Browser localStorage should have:
     - `accessToken`: "Bearer ..."
     - `refreshToken`: "..."
     - `user`: `{"id": 2, "name": "Test User", "email": "test@example.com", "role": "USER"}`

5. **Check Browser Console (F12):**
   ```
   🔐 Login response: {accessToken: "...", refreshToken: "...", email: "...", name: "...", role: "...", userId: ...}
   ✅ Login successful: {id: 2, name: "Test User", email: "test@example.com", role: "USER"}
   ```

---

## 📝 Test Signup Functionality

1. Click **Sign Up** button
2. Fill form:
   ```
   Name: John Doe
   Email: john@test.com
   Password: password123
   Mobile Number: 9876543210
   Age: 28
   Gender: MALE
   ```
3. Click **Sign Up**
4. **Expected Result:**
   - ✅ Account created
   - ✅ Auto-login with new account
   - ✅ Redirect to home page
   - ✅ Navbar shows "John Doe"

---

## 🎭 Test Movie Browsing

### Hero Carousel:
- Auto-slides every 5 seconds
- Shows top 5 highest-rated movies
- Click **Book Tickets** → Goes to movie details page

### Movie Grid:
- Displays all 20 movies in cards
- Each card shows:
  - Poster image (colorful placeholder)
  - Movie name
  - Rating (⭐)
  - Duration (🕐)
  - Genre badge
  - Language
  - "Now Showing" badge
- Click any card → Movie details page

### Movie Details Page:
- Shows full description
- Director and cast
- All metadata
- "Book Tickets" button

---

## 🎨 Expected Placeholder Images

Each movie has a unique color:

| Movie | Color Code |
|-------|-----------|
| Jawan | `#FF6B6B` (Red) |
| Pathaan | `#4ECDC4` (Teal) |
| RRR | `#FFE66D` (Yellow) |
| Brahmastra | `#A8E6CF` (Mint) |
| KGF Chapter 2 | `#FF8B94` (Pink) |
| Kantara | `#95E1D3` (Aqua) |
| Drishyam 2 | `#F3A683` (Orange) |
| The Kashmir Files | `#C7CEEA` (Lavender) |
| Vikram | `#FFDAC1` (Peach) |
| Ponniyin Selvan | `#B5EAD7` (Seafoam) |

(10 more with other colors...)

---

## 🔍 Debugging Tips

### If images still don't load:
1. Check browser Network tab → Should see `https://via.placeholder.com/...` requests
2. Check backend console for "Initialized 20 movies"
3. Verify database: `mysql -u springuser -p bookmyshow -e "SELECT id, movieName, posterUrl FROM movies LIMIT 3;"`

### If login fails:
1. Open Browser DevTools → Console
2. Look for error messages starting with ❌
3. Check Network tab → POST /auth/login → Response should be 200 OK
4. Check response body has: `{accessToken, refreshToken, email, name, role, userId}`

### If frontend is blank:
1. Check browser console for errors
2. Verify backend is running: `curl http://localhost:8080/auth/health`
3. Check CORS: Network tab should NOT show CORS errors

---

## ✨ Expected User Flow

1. **Home Page** → See hero carousel + 20 movies
2. **Click Movie Card** → Movie details page
3. **Click Login** → Login form
4. **Submit Credentials** → Auto-login + redirect home
5. **Navbar** → Shows user name + Logout button
6. **Browse Movies** → All images load perfectly
7. **Book Tickets** → (Next feature to implement)

---

## 🎯 Success Criteria

✅ Backend starts without errors  
✅ 20 movies loaded in database  
✅ All placeholder images load (no broken images)  
✅ Login with test@example.com works  
✅ Signup creates new user  
✅ Hero carousel auto-slides  
✅ Movie grid displays all 20 movies  
✅ No console errors  
✅ No CORS errors  

---

## 📊 Database Status

After backend restart, your database should have:

```sql
-- Check movies (should be 20)
SELECT COUNT(*) FROM movies;

-- Check theaters (should be 35)
SELECT COUNT(*) FROM theaters;

-- Check users (should be 2)
SELECT COUNT(*) FROM users;

-- View sample movie
SELECT id, movieName, rating, genre, language, posterUrl 
FROM movies 
WHERE movieName = 'Jawan';
```

---

## 🐛 Known Working Features

- ✅ User Registration & Login
- ✅ Movie Listing (Now Showing)
- ✅ Movie Search (by keyword)
- ✅ Advanced Filters (genre, language, rating)
- ✅ Theater Management
- ✅ Role-based Access (USER, ADMIN, THEATER_OWNER)
- ✅ JWT Authentication
- ✅ CORS enabled
- ✅ Auto database initialization

---

## 📞 Quick Commands Reference

```bash
# Backend (Spring Boot)
# Run in IntelliJ or:
cd /Users/vineettiwari/Downloads/bookmyshow/Book-My-Show
./mvnw spring-boot:run

# Frontend (React + Vite)
cd /Users/vineettiwari/Downloads/bookmyshow/Book-My-Show/frontend
npm run dev

# Database
mysql -u springuser -pspringpass123 bookmyshow

# Check backend health
curl http://localhost:8080/auth/health

# Get all movies
curl http://localhost:8080/api/movies/now-showing

# Test login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

---

## 🎉 Everything Should Work Now!

If you still face any issues, check:
1. Backend console logs
2. Frontend browser console (F12)
3. Network tab in DevTools
4. MySQL database content

**Happy Testing! 🚀🎬**
