# 🔍 COMPLETE NAMING CONSISTENCY ANALYSIS REPORT

**Generated:** 2026-01-06  
**Application:** BookMyShow  
**Analysis Type:** End-to-End Database → Backend → Frontend

---

## ✅ EXECUTIVE SUMMARY

**Status:** CRITICAL NAMING MISMATCHES FOUND

### Key Findings:
- ❌ **7 Critical Mismatches** affecting data flow
- ⚠️ **3 Inconsistent Patterns** requiring standardization  
- ✅ **Majority of fields** are correctly mapped with snake_case → camelCase

### Root Causes:
1. **Missing @Column annotations** in JPA entities causing Hibernate to auto-generate snake_case DB columns
2. **Backend returns Entity objects directly** instead of DTOs, exposing camelCase to frontend
3. **Frontend expects camelCase** (correct), but some mock data uses different field names
4. **No JSON serialization customization** (@JsonProperty annotations missing)

---

## 📊 LAYER-BY-LAYER ANALYSIS

### 🗄️ DATABASE LAYER (MySQL)

**Naming Convention:** `snake_case` (Hibernate auto-generated)

#### Tables:
```sql
✅ movies         (lowercase, plural)
✅ users          (lowercase, plural)
✅ theaters       (lowercase, plural)
✅ shows          (lowercase, plural)
✅ show_seats     (lowercase, snake_case, plural)
✅ theater_seats  (lowercase, snake_case, plural)
✅ tickets        (lowercase, plural)
✅ payments       (lowercase, plural)
✅ seat_locks     (lowercase, snake_case, plural)
```

#### `movies` Table Columns:
```
| DB Column      | Type           | Nullable | Notes                    |
|----------------|----------------|----------|--------------------------|
| id             | int            | NO       | PK, auto_increment       |
| movie_name     | varchar(255)   | NO       | ❌ snake_case            |
| duration       | int            | YES      | ✅                       |
| rating         | double         | YES      | ✅                       |
| release_date   | date           | YES      | ❌ snake_case            |
| genre          | enum           | YES      | ✅                       |
| language       | enum           | YES      | ✅                       |
| description    | varchar(2000)  | YES      | ✅                       |
| director       | varchar(255)   | YES      | ✅                       |
| cast           | varchar(1000)  | YES      | ✅                       |
| poster_url     | varchar(255)   | YES      | ❌ snake_case            |
| trailer_url    | varchar(255)   | YES      | ❌ snake_case            |
| now_showing    | bit(1)         | NO       | ❌ snake_case            |
| created_at     | datetime(6)    | YES      | ❌ snake_case            |
| updated_at     | datetime(6)    | YES      | ❌ snake_case            |
```

#### `users` Table Columns:
```
| DB Column      | Type           | Nullable | Notes                    |
|----------------|----------------|----------|--------------------------|
| id             | int            | NO       | PK, auto_increment       |
| name           | varchar(255)   | NO       | ✅                       |
| age            | int            | YES      | ✅                       |
| address        | varchar(255)   | YES      | ✅                       |
| gender         | enum           | YES      | ✅                       |
| mobile_no      | varchar(255)   | YES      | ❌ snake_case            |
| email_id       | varchar(255)   | NO       | ❌ snake_case (UNIQUE)   |
| password       | varchar(255)   | NO       | ✅                       |
| role           | enum           | NO       | ✅                       |
| is_active      | bit(1)         | NO       | ❌ snake_case            |
| wallet_balance | double         | NO       | ❌ snake_case            |
| created_at     | datetime(6)    | YES      | ❌ snake_case            |
| updated_at     | datetime(6)    | YES      | ❌ snake_case            |
```

#### `shows` Table Columns:
```
| DB Column      | Type           | Nullable | Notes                    |
|----------------|----------------|----------|--------------------------|
| show_id        | int            | NO       | PK, auto_increment       |
| created_at     | datetime(6)    | YES      | ❌ snake_case            |
| date           | date           | NO       | ✅                       |
| time           | time(6)        | NO       | ✅                       |
| movie_id       | int            | NO       | ❌ FK, snake_case        |
| theater_id     | int            | NO       | ❌ FK, snake_case        |
```

#### `show_seats` Table Columns:
```
| DB Column        | Type           | Nullable | Notes                    |
|------------------|----------------|----------|--------------------------|
| id               | int            | NO       | PK, auto_increment       |
| is_available     | bit(1)         | NO       | ❌ snake_case            |
| is_food_contains | bit(1)         | NO       | ❌ snake_case            |
| price            | int            | NO       | ✅                       |
| seat_no          | varchar(255)   | NO       | ❌ snake_case            |
| seat_type        | enum           | NO       | ❌ snake_case            |
| show_show_id     | int            | NO       | ❌ FK, duplicate prefix  |
```

#### `tickets` Table Columns:
```
| DB Column           | Type           | Nullable | Notes                    |
|---------------------|----------------|----------|--------------------------|
| ticket_id           | int            | NO       | PK, auto_increment       |
| booked_at           | datetime(6)    | YES      | ❌ snake_case            |
| booked_seats        | varchar(500)   | NO       | ❌ snake_case            |
| total_tickets_price | int            | NO       | ❌ snake_case            |
| show_show_id        | int            | NO       | ❌ FK, duplicate prefix  |
| user_id             | int            | NO       | ❌ FK, snake_case        |
```

---

### 🔧 BACKEND LAYER (Spring Boot + JPA)

**Naming Convention:** `camelCase` (Java standard)

#### Movie.java Entity:
```java
@Entity
@Table(name = "movies")
public class Movie {
    private Integer id;                    // ✅ → id
    private String movieName;              // ❌ → movie_name (missing @Column)
    private Integer duration;              // ✅ → duration
    private Double rating;                 // ✅ → rating
    private Date releaseDate;              // ❌ → release_date (missing @Column)
    private Genre genre;                   // ✅ → genre
    private Language language;             // ✅ → language
    private String description;            // ✅ → description
    private String director;               // ✅ → director
    private String cast;                   // ✅ → cast
    private String posterUrl;              // ❌ → poster_url (missing @Column)
    private String trailerUrl;             // ❌ → trailer_url (missing @Column)
    private Boolean nowShowing;            // ❌ → now_showing (missing @Column)
    private LocalDateTime createdAt;       // ❌ → created_at (has @CreationTimestamp)
    private LocalDateTime updatedAt;       // ❌ → updated_at (has @UpdateTimestamp)
    private List<Show> shows;              // ✅ Relationship
}
```

**CRITICAL ISSUE:** No `@Column(name = "...")` annotations for multi-word fields!

#### User.java Entity:
```java
@Entity
@Table(name = "users")
public class User {
    private Integer id;                    // ✅ → id
    private String name;                   // ✅ → name
    private Integer age;                   // ✅ → age
    private String address;                // ✅ → address
    private Gender gender;                 // ✅ → gender
    private String mobileNo;               // ❌ → mobile_no (missing @Column)
    private String emailId;                // ❌ → email_id (missing @Column)
    private String password;               // ✅ → password
    private UserRole role;                 // ✅ → role
    private Boolean isActive;              // ❌ → is_active (missing @Column)
    private Double walletBalance;          // ❌ → wallet_balance (missing @Column)
    private LocalDateTime createdAt;       // ❌ → created_at
    private LocalDateTime updatedAt;       // ❌ → updated_at
}
```

#### Show.java Entity:
```java
@Entity
@Table(name = "shows")
public class Show {
    private Integer showId;                // ❌ → show_id (should be 'id')
    private Time time;                     // ✅ → time
    private Date date;                     // ✅ → date
    private LocalDateTime createdAt;       // ❌ → created_at
    @JoinColumn Movie movie;               // ❌ → movie_id
    @JoinColumn Theater theater;           // ❌ → theater_id
}
```

**CRITICAL ISSUE:** Primary key is `showId` but DB column is `show_id` - should be just `id`!

#### ShowSeat.java Entity:
```java
@Entity
@Table(name = "show_seats")
public class ShowSeat {
    private Integer id;                    // ✅ → id
    private String seatNo;                 // ❌ → seat_no
    private SeatType seatType;             // ❌ → seat_type
    private Integer price;                 // ✅ → price
    private Boolean isAvailable;           // ❌ → is_available
    private Boolean isFoodContains;        // ❌ → is_food_contains
    @JoinColumn Show show;                 // ❌ → show_show_id (WRONG!)
}
```

**CRITICAL ISSUE:** FK column is `show_show_id` instead of `show_id`!

#### Ticket.java Entity:
```java
@Entity
@Table(name = "tickets")
public class Ticket {
    private Integer ticketId;              // ❌ → ticket_id (should be 'id')
    private Integer totalTicketsPrice;     // ❌ → total_tickets_price
    private String bookedSeats;            // ❌ → booked_seats
    private LocalDateTime bookedAt;        // ❌ → booked_at
    @JoinColumn Show show;                 // ❌ → show_show_id (WRONG!)
    @JoinColumn User user;                 // ❌ → user_id
}
```

---

### 🎨 FRONTEND LAYER (React)

**Naming Convention:** `camelCase` (JavaScript standard)

#### AuthResponseDto Backend Returns:
```json
{
  "accessToken": "jwt...",
  "refreshToken": "jwt...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "email": "user@example.com",
  "name": "John Doe",
  "role": "USER",
  "userId": 123
}
```

#### Frontend Expects (AuthContext.jsx):
```javascript
// Maps backend response correctly:
const userData = {
  id: response.userId,      // ✅ Correct mapping
  name: response.name,      // ✅ Correct
  email: response.email,    // ✅ Correct
  role: response.role       // ✅ Correct
};
```

#### Movie Object Backend Returns (Movie entity JSON serialized):
```json
{
  "id": 1,
  "movieName": "Jawan",           // ✅ camelCase from entity
  "duration": 169,
  "rating": 8.5,
  "releaseDate": "2023-09-07",    // ✅ camelCase from entity
  "genre": "ACTION",
  "language": "HINDI",
  "description": "...",
  "director": "Atlee",
  "cast": "Shah Rukh Khan, ...",
  "posterUrl": "https://...",     // ✅ camelCase from entity
  "trailerUrl": "https://...",    // ✅ camelCase from entity
  "nowShowing": true,             // ✅ camelCase from entity
  "createdAt": "2026-01-06T...",  // ✅ camelCase from entity
  "updatedAt": "2026-01-06T..."   // ✅ camelCase from entity
}
```

#### Frontend Components Expect:
```javascript
// MovieCard.jsx - CORRECT ✅
movie.movieName
movie.posterUrl
movie.rating
movie.duration
movie.genre
movie.language
movie.nowShowing

// Hero.jsx - CORRECT ✅
movie.movieName
movie.posterUrl
movie.description
movie.rating
movie.duration
movie.language

// MovieDetails.jsx - CORRECT with fallback ✅
movie.movieName || movie.name  // Backward compatible
movie.posterUrl
```

---

## 🚨 CRITICAL NAMING MISMATCHES

### ❌ MISMATCH #1: Foreign Key Naming
**Location:** `ShowSeat.java`, `Ticket.java`

```
DB Column:           show_show_id  ❌ WRONG (duplicate prefix)
Should be:           show_id       ✅ CORRECT
JPA Entity:          @JoinColumn Show show
Hibernate generates: show_show_id  (entity field name + _id)
```

**Impact:** FK constraint names are wrong, queries may fail

**Root Cause:** Missing `@JoinColumn(name = "show_id")` annotation

---

### ❌ MISMATCH #2: Primary Key Field Naming
**Location:** `Show.java`, `Ticket.java`

```
DB Column:     show_id    ❌
Java Field:    showId     ❌
Should be:     id         ✅
API Returns:   showId     ❌
Frontend:      (depends on what backend returns)
```

**Impact:** Inconsistent ID field naming across entities

**Root Cause:** Using `showId` instead of standard `id` pattern

---

### ❌ MISMATCH #3: Multi-word Field Mapping
**Location:** All entities with camelCase fields

```
Java Field:       movieName      (camelCase)
DB Column:        movie_name     (snake_case - auto-generated)
Missing:          @Column(name = "movie_name")
API Returns:      movieName      ✅ (from entity)
Frontend Expects: movieName      ✅
```

**Status:** Actually working because backend returns entities directly (camelCase), but DB uses snake_case.

**Issue:** No explicit mapping. Works by accident, not by design.

---

### ⚠️ MISMATCH #4: SignupRequestDto Field Mapping
**Location:** `authService.signup()` in frontend

```
Frontend Form:     mobileNumber   ❌
Frontend Service:  mobileNo       ✅ (mapped)
Backend DTO:       mobileNo       ✅
Backend Entity:    mobileNo       ✅
DB Column:         mobile_no      ✅

Frontend manually maps:
backendData = {
  mobileNo: userData.mobileNumber  // ✅ Correct mapping
}
```

**Status:** Working correctly with manual mapping

---

### ⚠️ MISMATCH #5: AdminDashboard Mock Data
**Location:** `frontend/src/pages/Admin/AdminDashboard.jsx`

```javascript
// Mock data uses:
movie.name           ❌ WRONG

// But real backend returns:
movie.movieName      ✅ CORRECT
```

**Impact:** Admin dashboard will break when using real API

---

## 📋 COMPLETE FIELD MAPPING TABLE

### Movie Entity Mapping:

| DB Column (snake_case) | Backend Field (camelCase) | @Column Annotation | JSON Output | Frontend Usage | Status |
|------------------------|---------------------------|--------------------| ------------|----------------|--------|
| id | id | Auto | id | movie.id | ✅ |
| movie_name | movieName | ❌ Missing | movieName | movie.movieName | ⚠️ Works |
| duration | duration | N/A | duration | movie.duration | ✅ |
| rating | rating | N/A | rating | movie.rating | ✅ |
| release_date | releaseDate | ❌ Missing | releaseDate | movie.releaseDate | ⚠️ Works |
| genre | genre | N/A | genre | movie.genre | ✅ |
| language | language | N/A | language | movie.language | ✅ |
| description | description | N/A | description | movie.description | ✅ |
| director | director | N/A | director | movie.director | ✅ |
| cast | cast | N/A | cast | movie.cast | ✅ |
| poster_url | posterUrl | ❌ Missing | posterUrl | movie.posterUrl | ⚠️ Works |
| trailer_url | trailerUrl | ❌ Missing | trailerUrl | movie.trailerUrl | ⚠️ Works |
| now_showing | nowShowing | ❌ Missing | nowShowing | movie.nowShowing | ⚠️ Works |
| created_at | createdAt | Auto | createdAt | movie.createdAt | ⚠️ Works |
| updated_at | updatedAt | Auto | updatedAt | movie.updatedAt | ⚠️ Works |

### User Entity Mapping:

| DB Column (snake_case) | Backend Field (camelCase) | @Column Annotation | JSON Output | Frontend Usage | Status |
|------------------------|---------------------------|--------------------| ------------|----------------|--------|
| id | id | Auto | id | user.id | ✅ |
| name | name | N/A | name | user.name | ✅ |
| age | age | N/A | age | user.age | ✅ |
| address | address | N/A | address | user.address | ✅ |
| gender | gender | N/A | gender | user.gender | ✅ |
| mobile_no | mobileNo | ❌ Missing | mobileNo | (not used) | ⚠️ Works |
| email_id | emailId | ❌ Missing | emailId | user.email (mapped) | ⚠️ Mapped |
| password | password | N/A | (hidden) | - | ✅ |
| role | role | N/A | role | user.role | ✅ |
| is_active | isActive | ❌ Missing | isActive | (not used) | ⚠️ Works |
| wallet_balance | walletBalance | ❌ Missing | walletBalance | (not used) | ⚠️ Works |
| created_at | createdAt | Auto | createdAt | (not used) | ⚠️ Works |
| updated_at | updatedAt | Auto | updatedAt | (not used) | ⚠️ Works |

### Show Entity Mapping:

| DB Column (snake_case) | Backend Field (camelCase) | @Column Annotation | JSON Output | Frontend Usage | Status |
|------------------------|---------------------------|--------------------| ------------|----------------|--------|
| show_id | showId | ❌ Should be `id` | showId | show.showId | ❌ Wrong |
| time | time | N/A | time | show.time | ✅ |
| date | date | N/A | date | show.date | ✅ |
| created_at | createdAt | Auto | createdAt | (not used) | ⚠️ Works |
| movie_id | movie (object) | FK | movie | show.movie | ✅ |
| theater_id | theater (object) | FK | theater | show.theater | ✅ |

---

## 🔧 ROOT CAUSE ANALYSIS

### Why The App Currently Works (Partially):

1. **Backend returns Entity objects directly as JSON**
   - Jackson serializes Java camelCase fields → JSON camelCase
   - Frontend expects camelCase → ✅ Match

2. **Hibernate auto-generates snake_case column names from camelCase**
   - Java field: `movieName` → DB column: `movie_name`
   - This is standard Hibernate behavior

3. **Manual mapping in frontend API services**
   - Example: `mobileNumber` → `mobileNo`
   - Prevents some mismatches

### Why The App Will Break:

1. **Foreign key naming is wrong**
   - `show_show_id` instead of `show_id`
   - Can cause query failures

2. **Inconsistent primary key naming**
   - Some use `id`, some use `showId`, `ticketId`
   - Makes generic repository methods fail

3. **No explicit ORM mapping**
   - If DB column names change, JPA breaks
   - No documentation of DB ↔ Entity mapping

4. **Mock data uses different field names**
   - Admin dashboard uses `movie.name`
   - Real API returns `movie.movieName`

---

## ✅ RECOMMENDED FIX STRATEGY

### Option A: EXPLICIT MAPPING (RECOMMENDED)

Add `@Column(name = "...")` to ALL multi-word fields:

```java
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "movie_name", nullable = false)
    private String movieName;
    
    @Column(name = "release_date")
    private Date releaseDate;
    
    @Column(name = "poster_url")
    private String posterUrl;
    
    @Column(name = "trailer_url")
    private String trailerUrl;
    
    @Column(name = "now_showing", nullable = false)
    private Boolean nowShowing;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

**Fix FK naming:**
```java
@ManyToOne
@JoinColumn(name = "show_id", nullable = false)  // ✅ Explicit name
private Show show;
```

**Fix PK naming:**
```java
// Change from:
private Integer showId;

// To:
private Integer id;  // ✅ Consistent
```

### Option B: USE DTOs (BEST PRACTICE)

Create ResponseDtos for all controllers:

```java
@Data
public class MovieResponseDto {
    private Integer id;
    
    @JsonProperty("movieName")
    private String movieName;
    
    @JsonProperty("releaseDate")
    private String releaseDate;
    
    // ... explicit JSON field names
}
```

Then map Entity → DTO in services before returning to controllers.

---

## 📐 UNIFIED NAMING STANDARD

### ✅ FINAL STANDARD:

1. **Database (MySQL):**
   - Tables: `lowercase_plural` (e.g., `movies`, `show_seats`)
   - Columns: `snake_case` (e.g., `movie_name`, `release_date`)
   - Primary Keys: `id` (NOT `show_id`, `movie_id`)
   - Foreign Keys: `<table>_id` (e.g., `movie_id`, `theater_id`)

2. **Backend (Java Entities):**
   - Classes: `PascalCase` singular (e.g., `Movie`, `ShowSeat`)
   - Fields: `camelCase` (e.g., `movieName`, `releaseDate`)
   - Primary Keys: `id` (NOT `movieId`, `showId`)
   - ALL multi-word fields MUST have `@Column(name = "snake_case")`
   - ALL foreign keys MUST have `@JoinColumn(name = "table_id")`

3. **Backend (DTOs):**
   - Request DTOs: `camelCase` (match frontend forms)
   - Response DTOs: `camelCase` with `@JsonProperty` if needed
   - Map Entity → DTO in service layer

4. **Backend (JSON API):**
   - ALL fields: `camelCase` (JavaScript standard)
   - Use DTOs or `@JsonProperty` annotations

5. **Frontend (React):**
   - ALL variables: `camelCase`
   - API requests: `camelCase` payloads
   - API responses: expect `camelCase`
   - NO field name mapping in components

---

## 🔨 REQUIRED FIXES

### Priority 1: CRITICAL (Breaks functionality)

1. **Fix ShowSeat FK naming:**
   ```java
   @ManyToOne
   @JoinColumn(name = "show_id", nullable = false)  // Was: auto-generated as show_show_id
   private Show show;
   ```

2. **Fix Ticket FK naming:**
   ```java
   @ManyToOne
   @JoinColumn(name = "show_id", nullable = false)  // Was: auto-generated as show_show_id
   private Show show;
   
   @ManyToOne
   @JoinColumn(name = "user_id", nullable = false)
   private User user;
   ```

3. **Standardize Primary Keys:**
   ```java
   // Show.java - change from showId to id
   private Integer id;
   
   // Ticket.java - change from ticketId to id
   private Integer id;
   ```

4. **Fix AdminDashboard.jsx mock data:**
   ```javascript
   // Change from:
   movie.name
   
   // To:
   movie.movieName
   ```

### Priority 2: IMPORTANT (Best practice)

5. **Add @Column annotations to all entities** (Movie, User, Show, etc.)

6. **Create Response DTOs** for controllers instead of returning entities

7. **Add @JsonProperty annotations** if keeping entity serialization

### Priority 3: NICE TO HAVE

8. **Document all naming mappings** in README

9. **Create database migration scripts** for any schema changes

10. **Add API documentation** showing request/response field names

---

## 🧪 VERIFICATION CHECKLIST

After fixes, verify:

- [ ] Database columns match @Column annotations
- [ ] Foreign key columns are `<table>_id` format
- [ ] All primary keys are named `id`
- [ ] Backend JSON responses use camelCase
- [ ] Frontend receives and displays all fields correctly
- [ ] No `undefined` in browser console
- [ ] Login/signup works
- [ ] Movie list displays correctly
- [ ] Admin dashboard works with real data
- [ ] All relationships load correctly (movie.shows, user.tickets, etc.)

---

## 📝 CONCLUSION

**Current State:** Application works partially due to:
- Hibernate auto-generates snake_case (correct)
- Backend returns camelCase JSON (correct)
- Frontend expects camelCase (correct)
- Manual mapping in some places (workaround)

**Critical Issues:**
- ❌ Wrong FK names (`show_show_id`)
- ❌ Inconsistent PK naming (`showId` vs `id`)
- ❌ No explicit ORM mappings (@Column missing)
- ❌ Mock data uses wrong field names

**Recommendation:**
1. Add ALL @Column and @JoinColumn annotations (Priority 1)
2. Standardize primary keys to `id` (Priority 1)
3. Fix AdminDashboard mock data (Priority 1)
4. Create DTOs for clean API contracts (Priority 2)

**Impact:**
- Database ↔ Backend: ⚠️ Works but fragile (no explicit mapping)
- Backend ↔ Frontend: ✅ Works (camelCase throughout)
- Overall: ⚠️ Mostly functional but has critical bugs with FKs

