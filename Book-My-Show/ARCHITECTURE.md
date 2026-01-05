# BookMyShow Application Architecture

## 🏗️ Complete System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         USER BROWSER                                 │
│                     http://localhost:3000                            │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             │
┌────────────────────────────▼────────────────────────────────────────┐
│                    REACT FRONTEND (NEW)                              │
│                         Vite + React                                 │
├──────────────────────────────────────────────────────────────────────┤
│  Components:                                                         │
│    • Navbar (with city selector, search, user menu)                 │
│    • Hero (auto-sliding banner)                                     │
│    • MovieCard (hover effects, ratings)                             │
│    • FilterBar (genre, language, rating)                            │
│    • Footer                                                          │
│                                                                      │
│  Pages:                                                              │
│    • Home (browse movies)                                           │
│    • Login/Signup (JWT auth)                                        │
│    • MovieDetails (full info)                                       │
│    • ShowSelection (choose theater & time)                          │
│    • SeatSelection (pick seats)                                     │
│    • Payment (checkout)                                             │
│    • BookingConfirmation                                            │
│    • MyBookings (user history)                                      │
│    • Profile (user info & wallet)                                   │
│    • AdminDashboard (stats & management)                            │
│    • TheaterOwnerDashboard                                          │
│                                                                      │
│  Context:                                                            │
│    • AuthContext (login state, JWT tokens)                          │
│    • AppContext (city, filters, search)                             │
│                                                                      │
│  Services:                                                           │
│    • authService (signup, login, logout)                            │
│    • movieService (get movies, search, filter)                      │
│    • theaterService (get theaters by city)                          │
│    • showService (get shows)                                        │
│    • bookingService (book, cancel)                                  │
│    • paymentService (initiate, process)                             │
│    • adminService (dashboard, manage)                               │
│                                                                      │
│  Mock Data:                                                          │
│    • 7 Indian Cities                                                │
│    • 35 Theaters (5-10 per city)                                    │
│    • 20 Bollywood/Regional Movies                                   │
│    • Showtimes, Seat Layouts                                        │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             │ HTTP/REST API
                             │ (with JWT in headers)
                             │
┌────────────────────────────▼────────────────────────────────────────┐
│                  SPRING BOOT BACKEND (EXISTING)                      │
│                   http://localhost:8080                              │
├──────────────────────────────────────────────────────────────────────┤
│  Controllers:                                                        │
│    • AuthController (/api/auth/*)                                   │
│    • MovieController (/movie/*)                                     │
│    • TheaterController (/theater/*)                                 │
│    • ShowController (/show/*)                                       │
│    • TicketController (/ticket/*)                                   │
│    • PaymentController (/api/payments/*)                            │
│    • SeatLockController (/api/seat-locks/*)                         │
│    • AdminController (/admin/*)                                     │
│    • BookingManagementController (/api/booking/*)                   │
│                                                                      │
│  Services:                                                           │
│    • AuthService (JWT generation, validation)                       │
│    • MovieService                                                   │
│    • TheaterService                                                 │
│    • ShowService                                                    │
│    • TicketService                                                  │
│    • PaymentService (with MockPaymentGateway)                       │
│    • SeatLockService (10-min timeout)                               │
│    • AdminService                                                   │
│    • BookingCancellationService (refunds)                           │
│                                                                      │
│  Security:                                                           │
│    • Spring Security + JWT                                          │
│    • BCrypt password encoding                                       │
│    • Role-based access (USER, ADMIN, THEATER_OWNER)                │
│    • JwtAuthenticationFilter                                        │
│                                                                      │
│  Repositories:                                                       │
│    • UserRepository                                                 │
│    • MovieRepository                                                │
│    • TheaterRepository                                              │
│    • ShowRepository                                                 │
│    • TicketRepository                                               │
│    • PaymentRepository                                              │
│    • SeatLockRepository                                             │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             │ JPA/Hibernate
                             │
┌────────────────────────────▼────────────────────────────────────────┐
│                      MySQL DATABASE                                  │
│                   jdbc:mysql://localhost:3306/bookmyshow             │
├──────────────────────────────────────────────────────────────────────┤
│  Tables:                                                             │
│    • users (with password, role, walletBalance)                     │
│    • movies                                                          │
│    • theaters                                                        │
│    • theater_seats                                                  │
│    • shows                                                           │
│    • show_seats                                                     │
│    • tickets                                                         │
│    • payments                                                        │
│    • seat_locks                                                     │
└──────────────────────────────────────────────────────────────────────┘


## 🔄 Data Flow Examples

### 1️⃣ User Login Flow
```
Browser (Login.jsx)
   │
   │ POST /api/auth/login
   │ { email, password }
   ▼
AuthController
   │
   │ validate credentials
   ▼
AuthService
   │
   │ generate JWT tokens
   ▼
Response: { accessToken, refreshToken, user }
   │
   ▼
Frontend stores in localStorage
   │
   ▼
Redirect based on role:
  - USER → Home
  - ADMIN → Admin Dashboard
  - THEATER_OWNER → Theater Dashboard
```

### 2️⃣ Browse Movies Flow
```
Browser (Home.jsx)
   │
   │ GET /movie/get-all-movies
   ▼
MovieController
   │
   ▼
MovieService
   │
   ▼
MovieRepository → MySQL
   │
   ▼
Response: List<Movie>
   │
   ▼
Frontend filters by:
  - City (from shows)
  - Genre
  - Language
  - Rating
   │
   ▼
Display MovieCard grid
```

### 3️⃣ Book Ticket Flow (Simplified)
```
1. Select Movie → MovieDetails page
2. Click "Book Tickets" → ShowSelection page
3. Choose theater & showtime
4. SeatSelection page → Lock seats (10 min)
   │
   │ POST /api/seat-locks/lock
   ▼
   SeatLockService creates locks
   
5. Payment page → Enter details
   │
   │ POST /api/payments/initiate
   │ POST /api/payments/process/{transactionId}
   ▼
   PaymentService → MockPaymentGateway
   
6. POST /ticket/book-ticket
   │
   ▼
   TicketService creates ticket
   
7. Confirm locks → Release others
   │
   │ POST /api/seat-locks/confirm/{sessionId}
   ▼
   BookingConfirmation page
```

### 4️⃣ Admin Dashboard Flow
```
Browser (AdminDashboard.jsx)
   │
   │ GET /admin/dashboard
   │ (with JWT token in header)
   ▼
JwtAuthenticationFilter
   │
   │ validate token & check role=ADMIN
   ▼
AdminController
   │
   ▼
AdminService
   │
   │ aggregate stats from all tables
   ▼
Response: {
  totalBookings,
  totalRevenue,
  totalUsers,
  popularMovies,
  recentBookings
}
   │
   ▼
Display dashboard with charts
```

## 🎨 UI Component Hierarchy

```
App
├── Navbar
│   ├── Logo
│   ├── SearchBar
│   ├── CitySelector (dropdown)
│   └── UserMenu (dropdown)
│       ├── Profile
│       ├── My Bookings
│       ├── Admin Dashboard (if admin)
│       └── Logout
│
├── Routes
│   ├── Home
│   │   ├── Hero (auto-slider)
│   │   ├── FilterBar
│   │   └── MovieGrid
│   │       └── MovieCard × 20
│   │
│   ├── MovieDetails
│   │   ├── Banner (with poster)
│   │   └── ShowsList
│   │
│   ├── Auth
│   │   ├── Login
│   │   └── Signup
│   │
│   ├── Booking
│   │   ├── ShowSelection
│   │   ├── SeatSelection
│   │   ├── Payment
│   │   └── Confirmation
│   │
│   ├── User
│   │   ├── Profile
│   │   └── MyBookings
│   │
│   ├── Admin
│   │   └── Dashboard
│   │
│   └── TheaterOwner
│       └── Dashboard
│
└── Footer
    ├── About Links
    ├── City Links
    ├── Help Links
    └── Social Links
```

## 🔐 Authentication & Authorization

```
┌──────────────────────────────────────┐
│   User Roles & Permissions           │
├──────────────────────────────────────┤
│                                      │
│  USER (default):                     │
│    ✓ Browse movies                   │
│    ✓ Book tickets                    │
│    ✓ View bookings                   │
│    ✓ Cancel bookings                 │
│    ✓ View profile                    │
│                                      │
│  ADMIN:                              │
│    ✓ All USER permissions            │
│    ✓ View admin dashboard            │
│    ✓ Manage users                    │
│    ✓ Manage movies                   │
│    ✓ Manage theaters                 │
│    ✓ View analytics                  │
│                                      │
│  THEATER_OWNER:                      │
│    ✓ All USER permissions            │
│    ✓ Manage owned theaters           │
│    ✓ Add/edit shows                  │
│    ✓ View revenue reports            │
│                                      │
└──────────────────────────────────────┘

JWT Token Flow:
1. Login → Backend generates accessToken (10h) + refreshToken (7d)
2. Frontend stores both in localStorage
3. Every API call includes: Authorization: Bearer {accessToken}
4. If accessToken expires → Auto-refresh using refreshToken
5. Logout → Clear localStorage
```

## 📊 State Management

```
┌────────────────────────────────────────┐
│      React Context Architecture        │
├────────────────────────────────────────┤
│                                        │
│  AuthContext:                          │
│    • user: { id, name, email, role }  │
│    • isAuthenticated: boolean          │
│    • login(credentials)                │
│    • signup(userData)                  │
│    • logout()                          │
│    • hasRole(role)                     │
│                                        │
│  AppContext:                           │
│    • selectedCity: 'Mumbai'            │
│    • searchQuery: ''                   │
│    • filters: { genre, language, ... } │
│    • changeCity(city)                  │
│    • updateFilters(filters)            │
│    • resetFilters()                    │
│                                        │
└────────────────────────────────────────┘
```

## 🗄️ Mock Data Structure

```javascript
// 7 Cities
cities = [
  { id: 1, name: 'Mumbai', state: 'Maharashtra' },
  { id: 2, name: 'Delhi', state: 'Delhi' },
  ...
]

// 35 Theaters (5-10 per city)
theaters = [
  {
    id: 1,
    name: 'PVR Phoenix Palladium',
    address: 'Lower Parel, Mumbai',
    city: 'Mumbai',
    screens: 8
  },
  ...
]

// 20 Movies
movies = [
  {
    id: 1,
    name: 'Jawan',
    genre: ['ACTION', 'THRILLER'],
    language: 'HINDI',
    duration: 169,
    rating: 8.5,
    posterUrl: '...',
    description: '...',
    cast: ['Shah Rukh Khan', ...],
    director: 'Atlee',
    nowShowing: true
  },
  ...
]

// Generated Shows (theater × movie × date × time)
shows = generateShows() // Thousands of combinations
```

---

**Architecture designed for:**
✅ Scalability - Microservices-ready
✅ Security - JWT + Role-based access
✅ Performance - Optimized queries
✅ Maintainability - Clean separation of concerns
✅ Extensibility - Easy to add features
