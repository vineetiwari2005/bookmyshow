# Database Setup Guide

## Auto-Generated Schema with JPA/Hibernate

Your application now uses **JPA with Hibernate ORM** to automatically create database tables from entity classes.

## Setup Steps

### 1. Create the Database (One-time only)

```bash
mysql -u root -p
```

```sql
CREATE DATABASE IF NOT EXISTS bookmyshow;
CREATE USER IF NOT EXISTS 'springuser'@'localhost' IDENTIFIED BY 'springpass123';
GRANT ALL PRIVILEGES ON bookmyshow.* TO 'springuser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 2. Start the Application

Just run your Spring Boot application in IntelliJ. Hibernate will:
- ✅ Automatically create all tables based on `@Entity` classes
- ✅ Insert 20 sample movies
- ✅ Insert 35 sample theaters across 7 cities
- ✅ Create 2 test users (admin & regular user)

## Database Tables Created

The following tables will be auto-created:

| Table | Description |
|-------|-------------|
| `movies` | Movie details with description, director, cast, poster, trailer |
| `theaters` | Theater details with name, address, city |
| `shows` | Show timings linking movies and theaters |
| `theater_seats` | Seats available in each theater |
| `show_seats` | Seat availability and pricing for each show |
| `tickets` | Booked tickets with seat info |
| `users` | User accounts with authentication |
| `payments` | Payment records (if implemented) |
| `seat_locks` | Temporary seat locks during booking (if implemented) |

## Sample Data Inserted

### Movies (20 total)
- Bollywood: Jawan, Pathaan, Tiger 3, Dunki, 12th Fail, Animal, Gadar 2, OMG 2
- South Indian: RRR, KGF 2, Salaar, Leo, Jailer, Vikram, Pushpa, Kantara, PS1
- Hollywood: Oppenheimer, Barbie
- Fantasy: Brahmastra

### Theaters (35 total)
- Mumbai: 5 theaters (PVR Phoenix, INOX Nariman Point, etc.)
- Delhi: 5 theaters (PVR Saket, INOX Nehru Place, etc.)
- Bangalore: 5 theaters (PVR Forum, INOX Garuda, etc.)
- Chennai: 5 theaters (PVR Grand Galleria, INOX Escape, etc.)
- Hyderabad: 5 theaters (PVR Next Galleria, INOX GSM, etc.)
- Kolkata: 5 theaters (PVR Avani, INOX South City, etc.)
- Pune: 5 theaters (PVR Phoenix, INOX Bund Garden, etc.)

### Test Users
| Email | Password | Role | Wallet |
|-------|----------|------|--------|
| admin@bookmyshow.com | admin123 | ADMIN | ₹0 |
| test@example.com | test123 | USER | ₹500 |

## Verify Tables

```bash
mysql -u springuser -pspringpass123 bookmyshow -e "SHOW TABLES;"
```

## Verify Data

```bash
# Check movies
mysql -u springuser -pspringpass123 bookmyshow -e "SELECT COUNT(*) FROM movies;"

# Check theaters
mysql -u springuser -pspringpass123 bookmyshow -e "SELECT COUNT(*) FROM theaters;"

# Check users
mysql -u springuser -pspringpass123 bookmyshow -e "SELECT name, emailId, role FROM users;"
```

## How It Works

### application.properties
```properties
spring.jpa.hibernate.ddl-auto=update
```

- `update`: Creates tables if they don't exist, updates schema if entities change
- `create`: Drops and recreates tables on every restart (⚠️ data loss!)
- `create-drop`: Creates on start, drops on shutdown (for testing)
- `validate`: Only validates schema, doesn't change anything
- `none`: Disables auto DDL

### Entity Annotations

```java
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String movieName;
    
    @Enumerated(value = EnumType.STRING)
    private Genre genre;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Show> shows;
}
```

### Data Initialization

The `DataInitializationService` implements `CommandLineRunner` and runs on application startup:
- Only inserts data if tables are empty
- Safe to restart without duplicating data

## Troubleshooting

### Tables not created?
- Check console logs for Hibernate SQL statements
- Verify `spring.jpa.show-sql=true` in application.properties
- Check MySQL user permissions

### Data not inserted?
- Check console for "✅ Initialized X movies/theaters/users"
- Verify `DataInitializationService` is being detected by Spring

### Schema mismatch?
- Delete database and restart: `DROP DATABASE bookmyshow; CREATE DATABASE bookmyshow;`
- Or set `spring.jpa.hibernate.ddl-auto=create` temporarily (⚠️ deletes all data!)

## Production Considerations

For production:
1. Set `spring.jpa.hibernate.ddl-auto=validate` or `none`
2. Use Flyway/Liquibase for database migrations
3. Never use `create` or `create-drop` in production
4. Disable `DataInitializationService` (add `@Profile("dev")` annotation)
