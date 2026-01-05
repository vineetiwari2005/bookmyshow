# 📡 BookMyShow API Reference

## Base URL
```
http://localhost:8080
```

---

## 🔐 Authentication APIs

### 1. User Signup
**Endpoint:** `POST /auth/signup`

**Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "mobileNo": "9876543210",
  "age": 25,
  "address": "Mumbai",
  "gender": "MALE",
  "role": "USER"
}
```

**Response:** `201 Created`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 36000,
  "email": "john@example.com",
  "name": "John Doe",
  "role": "USER",
  "userId": 1
}
```

---

### 2. User Login
**Endpoint:** `POST /auth/login`

**Request:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:** `200 OK`
```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "tokenType": "Bearer",
  "expiresIn": 36000,
  "email": "john@example.com",
  "name": "John Doe",
  "role": "USER",
  "userId": 1
}
```

---

### 3. Refresh Token
**Endpoint:** `POST /auth/refresh`

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:** `200 OK`
```json
{
  "accessToken": "new-access-token...",
  "refreshToken": "same-refresh-token...",
  ...
}
```

---

## 🎬 Movie Search & Filter APIs

### 1. Search Movies by Name
**Endpoint:** `GET /api/movies/search`

**Query Parameters:**
- `keyword` (required): Search term

**Example:**
```
GET /api/movies/search?keyword=avengers
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "movieName": "Avengers: Endgame",
    "duration": 181,
    "rating": 8.4,
    "releaseDate": "2019-04-26",
    "genre": "ACTION",
    "language": "ENGLISH"
  }
]
```

---

### 2. Filter by Genre
**Endpoint:** `GET /api/movies/filter/genre`

**Query Parameters:**
- `genre`: ACTION, COMEDY, DRAMA, THRILLER, HORROR, ROMANCE, SCI_FI

**Example:**
```
GET /api/movies/filter/genre?genre=ACTION
```

---

### 3. Filter by Language
**Endpoint:** `GET /api/movies/filter/language`

**Query Parameters:**
- `language`: HINDI, ENGLISH, TAMIL, TELUGU, etc.

**Example:**
```
GET /api/movies/filter/language?language=HINDI
```

---

### 4. Filter by Rating
**Endpoint:** `GET /api/movies/filter/rating`

**Query Parameters:**
- `minRating`: Minimum rating (0.0 - 10.0)

**Example:**
```
GET /api/movies/filter/rating?minRating=4.0
```

---

### 5. Now Showing Movies
**Endpoint:** `GET /api/movies/now-showing`

Returns movies with active shows.

---

### 6. Upcoming Movies
**Endpoint:** `GET /api/movies/upcoming`

Returns movies releasing in the future.

---

### 7. Movies by City
**Endpoint:** `GET /api/movies/city/{city}`

**Example:**
```
GET /api/movies/city/mumbai
```

---

### 8. Advanced Filter
**Endpoint:** `GET /api/movies/filter/advanced`

**Query Parameters (all optional):**
- `keyword`: Search term
- `genre`: Genre filter
- `language`: Language filter
- `minRating`: Minimum rating
- `city`: City filter

**Example:**
```
GET /api/movies/filter/advanced?keyword=war&genre=ACTION&language=HINDI&minRating=4.0&city=mumbai
```

---

### 9. Get Shows for Movie
**Endpoint:** `GET /api/movies/{movieId}/shows`

**Query Parameters:**
- `city` (required): City name

**Example:**
```
GET /api/movies/5/shows?city=mumbai
```

**Response:**
```json
[
  {
    "showId": 10,
    "time": "18:30:00",
    "date": "2026-01-05",
    "movie": {...},
    "theater": {...}
  }
]
```

---

## 🔒 Seat Locking APIs

### 1. Lock Seats
**Endpoint:** `POST /api/seat-lock/lock`

**Request:**
```json
{
  "showId": 10,
  "userId": 1,
  "seatNumbers": ["A1", "A2", "A3"]
}
```

**Response:** `200 OK`
```json
{
  "sessionId": "uuid-session-id",
  "showId": 10,
  "lockedSeats": ["A1", "A2", "A3"],
  "expiryTimeSeconds": 600,
  "message": "Seats locked successfully. Complete payment within 10 minutes"
}
```

---

### 2. Release Locks
**Endpoint:** `POST /api/seat-lock/release/{sessionId}`

**Response:** `200 OK`
```json
{
  "message": "Locks released successfully"
}
```

---

### 3. Get Locked Seats for Show
**Endpoint:** `GET /api/seat-lock/show/{showId}/locked-seats`

**Response:**
```json
["A1", "A2", "B5", "C10"]
```

---

### 4. Check Seat Availability
**Endpoint:** `POST /api/seat-lock/check-availability`

**Query Parameters:**
- `showId`: Show ID

**Request Body:**
```json
["A1", "A2", "A3"]
```

**Response:**
```json
{
  "A1": true,
  "A2": false,
  "A3": true
}
```

---

### 5. Get Remaining Lock Time
**Endpoint:** `GET /api/seat-lock/session/{sessionId}/remaining-time`

**Response:**
```json
{
  "remainingSeconds": 480
}
```

---

### 6. Extend Lock Time
**Endpoint:** `POST /api/seat-lock/session/{sessionId}/extend`

**Query Parameters:**
- `minutes` (default: 5, max: 5)

**Response:**
```json
{
  "message": "Lock time extended by 5 minutes"
}
```

---

## 💳 Payment APIs

### 1. Initiate Payment
**Endpoint:** `POST /api/payment/initiate`

**Request:**
```json
{
  "sessionId": "uuid-from-seat-lock",
  "userId": 1,
  "baseAmount": 500.0,
  "paymentMethod": "UPI",
  "promoCode": "SAVE10"
}
```

**Payment Methods:**
- `CREDIT_CARD`
- `DEBIT_CARD`
- `UPI`
- `NET_BANKING`
- `WALLET`

**Response:** `200 OK`
```json
{
  "paymentId": 1,
  "transactionId": "TXN_abc123xyz",
  "sessionId": "uuid-from-seat-lock",
  "baseAmount": 500.0,
  "convenienceFee": 20.0,
  "tax": 93.6,
  "discountAmount": 50.0,
  "totalAmount": 563.6,
  "paymentMethod": "UPI",
  "status": "PENDING",
  "message": "Payment initiated. Please proceed to complete payment.",
  "createdAt": "2026-01-05T10:30:00"
}
```

**Price Breakdown:**
```
Base Amount: 500.00
Convenience Fee: 20.00 (2.5%, min ₹20)
Tax (GST 18%): 93.60
Discount (SAVE10): -50.00
---------------------------------
Total: 563.60
```

---

### 2. Process Payment
**Endpoint:** `POST /api/payment/process/{transactionId}`

**Response (Success):** `200 OK`
```json
{
  "paymentId": 1,
  "transactionId": "TXN_abc123xyz",
  "status": "SUCCESS",
  "message": "Payment completed successfully",
  "completedAt": "2026-01-05T10:32:15",
  ...
}
```

**Response (Failure):** `402 Payment Required`
```json
{
  "paymentId": 1,
  "transactionId": "TXN_abc123xyz",
  "status": "FAILED",
  "message": "Payment failed: Insufficient funds",
  ...
}
```

---

### 3. Get Payment Status
**Endpoint:** `GET /api/payment/status/{transactionId}`

**Response:**
```json
{
  "paymentId": 1,
  "transactionId": "TXN_abc123xyz",
  "status": "SUCCESS",
  "totalAmount": 563.6,
  ...
}
```

---

### 4. Process Refund
**Endpoint:** `POST /api/payment/refund/{paymentId}`

**Query Parameters:**
- `reason` (required): Refund reason

**Example:**
```
POST /api/payment/refund/1?reason=User%20requested%20cancellation
```

**Response:**
```json
{
  "paymentId": 1,
  "status": "REFUNDED",
  "refundAmount": 563.6,
  "message": "Payment refunded successfully"
}
```

---

## 🎫 Booking Management APIs

### 1. Get Cancellation Policy
**Endpoint:** `GET /api/bookings/{ticketId}/cancellation-policy`

**Response:**
```json
{
  "ticketId": 123,
  "ticketAmount": 600.0,
  "hoursUntilShow": 30,
  "canCancel": true,
  "refundAmount": 570.0,
  "refundPercentage": 95,
  "policyMessage": "Cancellation eligible for 95% refund (5% convenience fee deducted)"
}
```

**Refund Policy:**
- **> 24 hours before show**: 95% refund
- **6-24 hours before show**: 50% refund
- **< 6 hours before show**: No refund

---

### 2. Cancel Ticket
**Endpoint:** `POST /api/bookings/{ticketId}/cancel`

**Query Parameters:**
- `userId` (required): User ID for authorization

**Example:**
```
POST /api/bookings/123/cancel?userId=1
```

**Response:**
```json
{
  "success": true,
  "ticketId": 123,
  "refundAmount": 570.0,
  "refundPercentage": 95,
  "hoursUntilShow": 30,
  "message": "Ticket cancelled successfully. Refund of ₹570.0 credited to wallet."
}
```

---

## 👨‍💼 Admin APIs

**All admin endpoints require ADMIN role and JWT token.**

**Authorization Header:**
```
Authorization: Bearer <admin-access-token>
```

---

### 1. Dashboard Analytics
**Endpoint:** `GET /admin/dashboard`

**Response:**
```json
{
  "totalUsers": 1250,
  "totalMovies": 45,
  "totalTheaters": 30,
  "totalShows": 180,
  "totalTicketsBooked": 5420,
  "monthlyRevenue": 2450000.0,
  "successfulPayments": 5200,
  "failedPayments": 220
}
```

---

### 2. Revenue Report
**Endpoint:** `GET /admin/revenue-report`

**Query Parameters:**
- `startDate`: ISO DateTime (2026-01-01T00:00:00)
- `endDate`: ISO DateTime (2026-01-31T23:59:59)

**Example:**
```
GET /admin/revenue-report?startDate=2026-01-01T00:00:00&endDate=2026-01-31T23:59:59
```

**Response:**
```json
{
  "totalRevenue": 2450000.0,
  "startDate": "2026-01-01T00:00:00",
  "endDate": "2026-01-31T23:59:59",
  "transactionCount": 5200
}
```

---

### 3. Get All Users
**Endpoint:** `GET /admin/users`

**Response:**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "isActive": true,
    "walletBalance": 100.0
  },
  ...
]
```

---

### 4. Update User Status
**Endpoint:** `PUT /admin/users/{userId}/status`

**Query Parameters:**
- `isActive`: true/false

**Example:**
```
PUT /admin/users/1/status?isActive=false
```

---

### 5. Get All Movies
**Endpoint:** `GET /admin/movies`

---

### 6. Get Popular Movies
**Endpoint:** `GET /admin/movies/popular`

**Query Parameters:**
- `limit` (default: 10): Number of movies

---

### 7. Delete Movie
**Endpoint:** `DELETE /admin/movies/{movieId}`

---

### 8. Get All Theaters
**Endpoint:** `GET /admin/theaters`

---

### 9. Delete Theater
**Endpoint:** `DELETE /admin/theaters/{theaterId}`

---

### 10. Get All Shows
**Endpoint:** `GET /admin/shows`

---

### 11. Delete Show
**Endpoint:** `DELETE /admin/shows/{showId}`

---

## 📝 Existing APIs (Unchanged)

All your existing endpoints continue to work:

```
POST /user/add
GET  /user/get-all-tickets
POST /movie/add
POST /theater/add
POST /show/add
POST /ticket/book
... and more
```

---

## 🔑 Promo Codes (Mock)

Currently hardcoded promo codes:

| Code | Discount |
|------|----------|
| `SAVE10` | 10% off |
| `SAVE20` | 20% off |
| `FIRSTBOOKING` | 15% off (max ₹100) |

---

## ⚡ Complete Booking Flow

```bash
# 1. Signup/Login
POST /auth/signup
# → Get accessToken

# 2. Search Movies
GET /api/movies/city/mumbai

# 3. Get Shows
GET /api/movies/{movieId}/shows?city=mumbai

# 4. Lock Seats
POST /api/seat-lock/lock
# → Get sessionId

# 5. Initiate Payment
POST /api/payment/initiate
# → Get transactionId

# 6. Process Payment
POST /api/payment/process/{transactionId}
# → Payment SUCCESS

# 7. Book Ticket (existing endpoint)
POST /ticket/book

# 8. (Optional) Cancel
POST /api/bookings/{ticketId}/cancel?userId=1
```

---

## 🛠️ Testing with cURL

### Create User:
```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "test123",
    "role": "USER"
  }'
```

### Login:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123"
  }'
```

### Search Movies:
```bash
curl http://localhost:8080/api/movies/search?keyword=action
```

### Lock Seats:
```bash
curl -X POST http://localhost:8080/api/seat-lock/lock \
  -H "Content-Type: application/json" \
  -d '{
    "showId": 1,
    "userId": 1,
    "seatNumbers": ["A1", "A2"]
  }'
```

### Admin Dashboard (with token):
```bash
curl http://localhost:8080/admin/dashboard \
  -H "Authorization: Bearer <your-token>"
```

---

## 📊 HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | OK - Success |
| 201 | Created - Resource created |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Invalid/missing token |
| 402 | Payment Required - Payment failed |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource already exists |

---

## 🔐 Security Notes

1. **JWT Token Validity**: 10 hours
2. **Refresh Token Validity**: 7 days
3. **Seat Lock Duration**: 10 minutes
4. **Password**: Minimum 6 characters
5. **Rate Limiting**: Not implemented (add in production)

---

## 🎯 API Design Principles

✅ RESTful design
✅ Clear endpoint naming
✅ Consistent response format
✅ Proper HTTP methods
✅ Meaningful status codes
✅ Input validation
✅ Error messages
✅ Backward compatibility

---

This API reference covers all new endpoints while preserving all existing functionality!
