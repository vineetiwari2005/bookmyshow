# 🚀 Quick Start Guide - BookMyShow Enhanced

## Prerequisites
- Java 17
- Maven
- MySQL (running on localhost:3306)
- Database: `bookmyshow`
- User: `springuser` / Password: `springpass123`

---

## 1. Start the Application

```bash
cd Book-My-Show
./mvnw clean install
./mvnw spring-boot:run
```

Application starts on: `http://localhost:8080`

---

## 2. Create Your First Admin User

```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin User",
    "email": "admin@bookmyshow.com",
    "password": "admin123",
    "role": "ADMIN"
  }'
```

**Save the `accessToken` from response!**

---

## 3. Test Admin Dashboard

```bash
# Replace <YOUR_TOKEN> with accessToken from step 2
curl http://localhost:8080/admin/dashboard \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

You should see analytics like:
```json
{
  "totalUsers": 1,
  "totalMovies": 0,
  "totalTheaters": 0,
  ...
}
```

---

## 4. Add Sample Movie (Using Existing Endpoint)

```bash
curl -X POST http://localhost:8080/movie/add \
  -H "Content-Type: application/json" \
  -d '{
    "movieName": "Avengers: Endgame",
    "duration": 181,
    "rating": 8.4,
    "releaseDate": "2019-04-26",
    "genre": "ACTION",
    "language": "ENGLISH"
  }'
```

---

## 5. Search for Movies (New Feature)

```bash
curl http://localhost:8080/api/movies/search?keyword=avengers
```

---

## 6. Complete Booking Flow Example

### Step 1: Create Regular User
```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "john123",
    "role": "USER"
  }'
```

### Step 2: Add Theater and Show (Use existing endpoints)
```bash
# Add Theater
curl -X POST http://localhost:8080/theater/add \
  -H "Content-Type: application/json" \
  -d '{
    "name": "PVR Cinemas",
    "address": "Mumbai, Maharashtra"
  }'

# Add Show (requires movie and theater to exist)
curl -X POST http://localhost:8080/show/add \
  -H "Content-Type: application/json" \
  -d '{
    "showDate": "2026-01-10",
    "showTime": "18:30:00",
    "movieId": 1,
    "theaterId": 1
  }'
```

### Step 3: Lock Seats (New Feature)
```bash
curl -X POST http://localhost:8080/api/seat-lock/lock \
  -H "Content-Type: application/json" \
  -d '{
    "showId": 1,
    "userId": 2,
    "seatNumbers": ["A1", "A2"]
  }'
```

**Response includes `sessionId` - save it!**

### Step 4: Initiate Payment (New Feature)
```bash
curl -X POST http://localhost:8080/api/payment/initiate \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "YOUR_SESSION_ID",
    "userId": 2,
    "baseAmount": 400.0,
    "paymentMethod": "UPI",
    "promoCode": "SAVE10"
  }'
```

**Response includes `transactionId` - save it!**

### Step 5: Process Payment (New Feature)
```bash
curl -X POST http://localhost:8080/api/payment/process/YOUR_TRANSACTION_ID
```

**Response shows payment status (90% success in mock gateway)**

### Step 6: Book Ticket (Existing Endpoint)
```bash
curl -X POST http://localhost:8080/ticket/book \
  -H "Content-Type: application/json" \
  -d '{
    "showId": 1,
    "userId": 2,
    "requestSeats": ["A1", "A2"]
  }'
```

---

## 7. Test Cancellation (New Feature)

```bash
# Check cancellation policy
curl http://localhost:8080/api/bookings/YOUR_TICKET_ID/cancellation-policy

# Cancel ticket
curl -X POST "http://localhost:8080/api/bookings/YOUR_TICKET_ID/cancel?userId=2"
```

---

## 8. Test Search Features (New)

```bash
# Search by name
curl http://localhost:8080/api/movies/search?keyword=avengers

# Filter by genre
curl http://localhost:8080/api/movies/filter/genre?genre=ACTION

# Now showing
curl http://localhost:8080/api/movies/now-showing

# Movies by city
curl http://localhost:8080/api/movies/city/mumbai

# Advanced filter
curl "http://localhost:8080/api/movies/filter/advanced?genre=ACTION&minRating=4.0"
```

---

## 🎯 Key Endpoints to Remember

### Public (No Auth Required)
```
POST /auth/signup
POST /auth/login
GET  /api/movies/search?keyword=...
GET  /api/movies/now-showing
GET  /api/movies/city/{city}
```

### User (Auth Optional for Existing, Required for New)
```
POST /api/seat-lock/lock
POST /api/payment/initiate
POST /api/payment/process/{id}
POST /api/bookings/{id}/cancel
```

### Admin Only (Auth Required)
```
GET  /admin/dashboard
GET  /admin/revenue-report
GET  /admin/users
DELETE /admin/movies/{id}
```

---

## 🔧 Troubleshooting

### Database Connection Error
```bash
# Make sure MySQL is running
mysql -u springuser -p

# Create database if not exists
CREATE DATABASE IF NOT EXISTS bookmyshow;
```

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081
```

### JWT Token Expired
```bash
# Use refresh token endpoint
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "YOUR_REFRESH_TOKEN"}'
```

---

## 📱 Testing with Postman

1. **Import Collection**: Create new collection in Postman
2. **Set Base URL**: `http://localhost:8080`
3. **Add Auth**: 
   - Type: Bearer Token
   - Token: `{{accessToken}}`
4. **Create Requests**:
   - Signup → Save token to variable
   - All other requests use saved token

---

## 🎨 Swagger UI (Optional)

Access API documentation at:
```
http://localhost:8080/swagger-ui.html
```

---

## 📊 Sample Test Data

### Movies
```json
{
  "movieName": "Spider-Man: No Way Home",
  "duration": 148,
  "rating": 8.7,
  "releaseDate": "2021-12-17",
  "genre": "ACTION",
  "language": "ENGLISH"
}
```

### Theaters
```json
{
  "name": "INOX Megaplex",
  "address": "Mumbai, Maharashtra"
}
```

### Shows
```json
{
  "showDate": "2026-01-15",
  "showTime": "21:00:00",
  "movieId": 1,
  "theaterId": 1
}
```

---

## ✅ Success Indicators

After running these steps, you should have:

✅ Admin user created
✅ Dashboard showing analytics
✅ Movie searchable
✅ Seats lockable
✅ Payment processable
✅ Bookings cancellable

---

## 🎯 Next Steps

1. Add more movies, theaters, and shows
2. Create multiple users
3. Test concurrent seat booking
4. Try different promo codes
5. Test refund flow
6. Explore admin analytics

---

## 📖 Full Documentation

- `ENHANCEMENT_GUIDE.md` - Detailed feature guide
- `IMPLEMENTATION_SUMMARY.md` - What was built
- `API_REFERENCE.md` - Complete API documentation

---

## 🎉 You're Ready!

Your BookMyShow application is now running with:
- ✅ Authentication & Authorization
- ✅ Seat Locking (Race-condition safe)
- ✅ Payment Processing
- ✅ Admin Panel
- ✅ Search & Filter
- ✅ Booking Cancellation

**All while keeping existing functionality intact!** 🚀
