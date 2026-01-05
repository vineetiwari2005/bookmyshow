# 🎬 BookMyShow - Production-Ready Backend System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive, production-ready ticket booking system backend inspired by BookMyShow. Built with Spring Boot, featuring JWT authentication, concurrent booking support, payment processing, and advanced search capabilities.

---

## ✨ Features

### 🎯 Core Features
- ✅ **User Management** - Registration, login, profile management
- ✅ **Movie Management** - Browse, search, filter movies
- ✅ **Theater Management** - Multiple theaters, screens, seat layouts
- ✅ **Show Management** - Multiple showtimes per movie
- ✅ **Ticket Booking** - End-to-end booking flow
- ✅ **Email Notifications** - Booking confirmations

### 🚀 Production-Ready Enhancements
- ✅ **JWT Authentication** - Secure token-based auth
- ✅ **Role-Based Access** - USER, ADMIN, THEATER_OWNER roles
- ✅ **Seat Locking** - Race-condition safe concurrent booking
- ✅ **Payment System** - Complete payment lifecycle with refunds
- ✅ **Search & Filter** - Advanced movie search by city, genre, language, rating
- ✅ **Booking Cancellation** - Flexible refund policies
- ✅ **Admin Panel** - Analytics dashboard and content management
- ✅ **Wallet System** - Refund management

---

## 📁 Project Structure

```
Book-My-Show/
├── src/main/java/com/driver/bookMyShow/
│   ├── Config/              # Security, scheduling configuration
│   ├── Controllers/         # REST API endpoints
│   │   ├── AuthController.java
│   │   ├── AdminController.java
│   │   ├── MovieSearchController.java
│   │   ├── PaymentController.java
│   │   ├── SeatLockController.java
│   │   ├── BookingManagementController.java
│   │   └── [Existing controllers...]
│   ├── Services/            # Business logic
│   │   ├── AuthService.java
│   │   ├── AdminService.java
│   │   ├── PaymentService.java
│   │   ├── SeatLockService.java
│   │   ├── MovieSearchService.java
│   │   └── [Existing services...]
│   ├── Models/              # JPA entities
│   │   ├── User.java (enhanced)
│   │   ├── SeatLock.java (new)
│   │   ├── Payment.java (new)
│   │   └── [Existing models...]
│   ├── Repositories/        # Data access layer
│   ├── Security/            # JWT, authentication
│   ├── Gateway/             # Payment gateway integration
│   ├── Utils/               # JWT utilities
│   ├── Dtos/                # Request/Response DTOs
│   └── Enums/               # Enumerations
└── resources/
    └── application.properties
```

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### 1. Clone & Setup Database
```bash
git clone https://github.com/vineetiwari2005/bookmyshow.git
cd Book-My-Show

# Create MySQL database
mysql -u root -p
CREATE DATABASE bookmyshow;
CREATE USER 'springuser'@'localhost' IDENTIFIED BY 'springpass123';
GRANT ALL PRIVILEGES ON bookmyshow.* TO 'springuser'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configure Application
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookmyshow
spring.datasource.username=springuser
spring.datasource.password=springpass123
```

### 3. Build & Run
```bash
./mvnw clean install
./mvnw spring-boot:run
```

Application starts at: **http://localhost:8080**

### 4. Test the API
```bash
# Create admin user
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin User",
    "email": "admin@bookmyshow.com",
    "password": "admin123",
    "role": "ADMIN"
  }'

# Login and get token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@bookmyshow.com",
    "password": "admin123"
  }'

# Access admin dashboard (use token from login)
curl http://localhost:8080/admin/dashboard \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## 📖 Documentation

| Document | Description |
|----------|-------------|
| [**QUICK_START.md**](QUICK_START.md) | Step-by-step getting started guide |
| [**API_REFERENCE.md**](API_REFERENCE.md) | Complete API endpoint documentation |
| [**ENHANCEMENT_GUIDE.md**](ENHANCEMENT_GUIDE.md) | Detailed feature explanations |
| [**IMPLEMENTATION_SUMMARY.md**](IMPLEMENTATION_SUMMARY.md) | Technical implementation details |

---

## 🔐 Authentication

### Signup
```bash
POST /auth/signup
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER"
}
```

### Login
```bash
POST /auth/login
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response includes JWT token valid for 10 hours.**

### Using Token
```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 🎫 Complete Booking Flow

```
1. User Registration/Login
   POST /auth/signup or POST /auth/login
   
2. Search Movies
   GET /api/movies/search?keyword=avengers
   GET /api/movies/city/mumbai
   
3. Select Show
   GET /api/movies/{movieId}/shows?city=mumbai
   
4. Lock Seats (10-minute reservation)
   POST /api/seat-lock/lock
   → Returns sessionId
   
5. Initiate Payment
   POST /api/payment/initiate
   → Returns transactionId
   
6. Process Payment
   POST /api/payment/process/{transactionId}
   → On success: seats confirmed
   → On failure: seats released
   
7. Confirm Booking
   POST /ticket/book
   
8. (Optional) Cancel Booking
   POST /api/bookings/{ticketId}/cancel
   → Refund based on cancellation policy
```

---

## 🎯 Key Endpoints

### 🔓 Public APIs (No Auth Required)
```
POST   /auth/signup                          - User registration
POST   /auth/login                           - User login
GET    /api/movies/search?keyword={query}    - Search movies
GET    /api/movies/city/{city}               - Movies by city
GET    /api/movies/now-showing                - Currently showing
GET    /api/movies/filter/genre?genre={type} - Filter by genre
```

### 🔒 User APIs (Auth Required)
```
POST   /api/seat-lock/lock                   - Lock seats
POST   /api/payment/initiate                 - Initiate payment
POST   /api/payment/process/{id}             - Process payment
POST   /api/bookings/{id}/cancel             - Cancel booking
GET    /user/get-all-tickets                 - Booking history
```

### 👨‍💼 Admin APIs (ADMIN Role Required)
```
GET    /admin/dashboard                      - Analytics dashboard
GET    /admin/revenue-report                 - Revenue reports
GET    /admin/users                          - All users
DELETE /admin/movies/{id}                    - Delete movie
DELETE /admin/theaters/{id}                  - Delete theater
```

---

## 💡 Advanced Features

### Seat Locking
Prevents race conditions in concurrent bookings:
- **Lock Duration**: 10 minutes
- **Auto-Cleanup**: Expired locks released automatically
- **Concurrency Safe**: Database-level unique constraints

### Payment System
Complete payment lifecycle:
- **Pricing**: Base + 2.5% fee (min ₹20) + 18% GST
- **Promo Codes**: SAVE10, SAVE20, FIRSTBOOKING
- **Refunds**: Automatic wallet credit
- **Idempotency**: Safe retry support

### Cancellation Policy
Time-based refunds:
- **> 24 hours**: 95% refund
- **6-24 hours**: 50% refund
- **< 6 hours**: No refund

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Spring Boot 3.1.0 |
| **Security** | Spring Security + JWT |
| **Database** | MySQL 8.0 |
| **ORM** | Spring Data JPA / Hibernate |
| **Build Tool** | Maven |
| **Authentication** | JWT (jjwt 0.11.5) |
| **Validation** | Jakarta Validation |
| **Email** | Spring Mail |
| **API Docs** | SpringDoc OpenAPI 2.0 |

---

## 📊 Database Schema

### New Tables
- `SEAT_LOCKS` - Temporary seat reservations
- `PAYMENTS` - Payment transactions

### Enhanced Tables
- `USERS` - Added: password, role, isActive, walletBalance

### Existing Tables
- `MOVIES` - Movie information
- `THEATERS` - Theater details
- `SHOWS` - Show schedules
- `TICKETS` - Booking records
- `THEATER_SEATS` - Seat inventory
- `SHOW_SEATS` - Seat availability per show

---

## 🎨 API Documentation

Access Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

Or view complete API reference: [API_REFERENCE.md](API_REFERENCE.md)

---

## 🔧 Configuration

### Application Properties
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/bookmyshow
spring.datasource.username=springuser
spring.datasource.password=springpass123

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### JWT Configuration
Currently uses auto-generated secret key. For production:
1. Generate strong secret key
2. Add to application.properties
3. Use environment variables

---

## 🧪 Testing

### Unit Tests
```bash
./mvnw test
```

### Integration Tests
```bash
./mvnw verify
```

### Manual Testing
See [QUICK_START.md](QUICK_START.md) for step-by-step testing guide.

---

## 🚀 Deployment

### Docker (Coming Soon)
```bash
docker-compose up
```

### Production Checklist
- [ ] Configure production database
- [ ] Set strong JWT secret
- [ ] Enable HTTPS
- [ ] Configure CORS
- [ ] Set up monitoring
- [ ] Configure logging
- [ ] Add rate limiting
- [ ] Set up backups

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Vineet Tiwari**
- GitHub: [@vineetiwari2005](https://github.com/vineetiwari2005)

---

## 🙏 Acknowledgments

- Spring Boot Team
- MySQL
- JWT Library Contributors
- Original BookMyShow for inspiration

---

## 📞 Support

For questions or issues:
- Open an [Issue](https://github.com/vineetiwari2005/bookmyshow/issues)
- Check [Documentation](ENHANCEMENT_GUIDE.md)

---

## 🎯 Project Status

**Status**: ✅ Production Ready

### Implemented Features
- ✅ Authentication & Authorization
- ✅ Role-Based Access Control
- ✅ Seat Locking System
- ✅ Payment Processing
- ✅ Search & Filter
- ✅ Booking Cancellation
- ✅ Admin Panel
- ✅ Analytics Dashboard

### Future Enhancements
- 🔲 Theater Owner Panel
- 🔲 Real Payment Gateway Integration (Razorpay/Stripe)
- 🔲 QR Code Tickets
- 🔲 SMS Notifications
- 🔲 Food & Beverage Ordering
- 🔲 Redis Caching
- 🔲 Docker Support
- 🔲 CI/CD Pipeline

---

## 🌟 Star History

If you find this project helpful, please consider giving it a star! ⭐

---

**Built with ❤️ using Spring Boot**
