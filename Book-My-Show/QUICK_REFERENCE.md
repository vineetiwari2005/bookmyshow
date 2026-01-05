# 🚀 BookMyShow - Quick Reference Card

## ⚡ Instant Commands

### Start Everything
```bash
# Terminal 1: Backend
cd Book-My-Show && ./mvnw spring-boot:run

# Terminal 2: Frontend  
cd Book-My-Show/frontend && npm install && npm run dev
```

### Access Points
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Database**: localhost:3306/bookmyshow

---

## 📁 Key Files

| File | Purpose |
|------|---------|
| `frontend/src/App.jsx` | Main app with routing |
| `frontend/src/services/index.js` | All API calls |
| `frontend/src/mockData/indianData.js` | Mock cities, theaters, movies |
| `frontend/src/context/AuthContext.jsx` | Authentication state |
| `frontend/vite.config.js` | Proxy to backend |

---

## 🎯 Key Features

### ✅ Implemented & Working
- Home page with hero slider
- 20 movies with search/filter
- Login/Signup (JWT)
- Movie details page
- User profile
- Admin dashboard
- Theater owner dashboard
- Role-based access
- Mobile responsive
- Toast notifications

### 📝 Placeholder (Ready for Enhancement)
- Seat selection UI
- Payment form
- Booking confirmation
- My bookings list
- Admin CRUD operations

---

## 🗺️ Navigation Map

```
/ → Home (public)
  ├── /login → Login page
  ├── /signup → Signup page
  └── /movie/:name → Movie details
  
/profile → User profile (protected)
/my-bookings → Booking history (protected)

/admin → Admin dashboard (admin only)
  ├── /admin/users
  ├── /admin/movies
  └── /admin/theaters

/theater-owner → Theater owner dashboard (owner only)
  ├── /theater-owner/theaters
  ├── /theater-owner/shows
  └── /theater-owner/reports
```

---

## 🎨 Design Tokens

```scss
// Colors
--primary-color: #f84464    // BookMyShow red
--secondary-color: #333545   // Dark gray
--success: #27ae60
--warning: #f39c12
--error: #e74c3c

// Typography
Font: Poppins, Roboto
H1: 2.5rem
Body: 1rem

// Spacing
--spacing-sm: 1rem
--spacing-md: 1.5rem
--spacing-lg: 2rem

// Breakpoints
Mobile: < 480px
Tablet: 768px
Desktop: 1024px
```

---

## 📊 Mock Data Summary

| Type | Count | Examples |
|------|-------|----------|
| **Cities** | 7 | Mumbai, Delhi, Bangalore, Chennai, Kolkata, Hyderabad, Pune |
| **Theaters** | 35 | PVR Phoenix, INOX Nariman, Cinepolis Andheri |
| **Movies** | 20 | Jawan, Pathaan, RRR, KGF, Brahmastra, Vikram |
| **Genres** | 11 | ACTION, COMEDY, DRAMA, THRILLER, ROMANCE, etc. |
| **Languages** | 8 | HINDI, TAMIL, TELUGU, KANNADA, ENGLISH, etc. |

---

## 🔐 Test Credentials

### Create User (Signup)
```json
{
  "name": "Test User",
  "email": "test@example.com",
  "password": "password123",
  "mobileNumber": "9876543210",
  "age": 25,
  "gender": "MALE"
}
```

### Admin User
Set role in database: `UPDATE users SET role='ADMIN' WHERE email='admin@test.com'`

---

## 🛠️ Common Tasks

### Add New Movie
```javascript
// Edit: frontend/src/mockData/indianData.js
export const movies = [
  ...existingMovies,
  {
    id: 21,
    name: 'New Movie',
    genre: ['ACTION'],
    language: 'HINDI',
    duration: 150,
    rating: 8.0,
    posterUrl: 'https://...',
    description: '...',
    cast: ['Actor 1', 'Actor 2'],
    director: 'Director Name',
    nowShowing: true
  }
];
```

### Change City
Click city selector in navbar → Select city

### Apply Filters
Home page → "Filters" button → Select genre/language/rating

### Check User Role
```javascript
// In any component
import { useAuth } from '../context/AuthContext';

const { user, isAdmin, isTheaterOwner } = useAuth();
```

---

## 🔌 API Endpoints Reference

### Authentication
- `POST /api/auth/signup` - Create account
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh token

### Movies
- `GET /movie/get-all-movies` - All movies
- `GET /movie/get-movie-by-name/:name` - Movie details
- `GET /api/movies/search?keyword=` - Search
- `GET /api/movies/filter?genre=&language=` - Filter

### Theaters
- `GET /theater/get-all-theaters` - All theaters
- `GET /theater/get-theaters-by-city/:city` - By city

### Booking
- `POST /ticket/book-ticket` - Book ticket
- `POST /api/seat-locks/lock` - Lock seats
- `POST /api/payments/initiate` - Start payment

### Admin
- `GET /admin/dashboard` - Dashboard data
- `GET /admin/users` - All users
- `POST /admin/users/:id/activate` - Activate user

---

## 🐛 Quick Troubleshooting

| Issue | Solution |
|-------|----------|
| **Blank/white screen** | 1. Check browser console (F12)<br>2. Visit /test route<br>3. See TROUBLESHOOTING.md |
| **Frontend can't connect to backend** | Check backend running on :8080<br>Check vite.config.js proxy |
| **CORS errors** | Add frontend URL to backend CORS config |
| **Token expired** | Auto-refreshes, or logout and login again |
| **Build fails** | `rm -rf node_modules && npm install` |
| **Movies not showing** | Check selected city, try changing city |
| **Can't login** | Check backend logs, verify user in database |

**📖 Full troubleshooting guide**: See [TROUBLESHOOTING.md](./TROUBLESHOOTING.md)

---

## 📚 Documentation Index

1. **GETTING_STARTED.md** - Complete setup guide
2. **ARCHITECTURE.md** - System architecture diagrams
3. **frontend/README.md** - Frontend detailed docs
4. **FRONTEND_COMPLETE.md** - What was created
5. **ENHANCEMENT_GUIDE.md** - Backend features (existing)
6. **API_REFERENCE.md** - API documentation (existing)

---

## ✅ Health Check

```bash
# Backend
curl http://localhost:8080/movie/get-all-movies

# Frontend (after npm run dev)
# Open http://localhost:3000 in browser
# Should see home page with movies

# Database
mysql -u root -p
USE bookmyshow;
SHOW TABLES;
```

---

## 🎯 User Journey Examples

### Book a Movie
1. Browse home → Click movie → Click "Book Tickets"
2. Login if needed → Select show → Select seats
3. Make payment → Get confirmation

### Admin Check Stats
1. Login as admin → Auto-redirect to /admin
2. See dashboard with bookings, revenue, users
3. Navigate to manage movies/theaters/users

### Filter Movies
1. Home page → Click "Filters"
2. Select genre: ACTION
3. Select language: HINDI
4. Set rating: 8.0+
5. Click "Apply Filters"

---

**🎬 You're all set! Enjoy building with BookMyShow!**

*Need help? Check documentation files or review browser console.*
