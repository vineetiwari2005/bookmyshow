package com.driver.bookMyShow.Controllers;

import com.driver.bookMyShow.Models.Movie;
import com.driver.bookMyShow.Models.Show;
import com.driver.bookMyShow.Models.Theater;
import com.driver.bookMyShow.Models.User;
import com.driver.bookMyShow.Services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminController - Admin panel endpoints
 * 
 * All endpoints require ADMIN role
 * Provides:
 * - Dashboard analytics
 * - Movie/Theater/Show management
 * - User management
 * - Revenue reports
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Get dashboard analytics
     * GET /admin/dashboard
     * 
     * Returns comprehensive statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> analytics = adminService.getDashboardAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get revenue report
     * GET /admin/revenue-report
     * 
     * Query params: startDate, endDate (ISO format)
     */
    @GetMapping("/revenue-report")
    public ResponseEntity<Map<String, Object>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Map<String, Object> report = adminService.getRevenueReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    /**
     * Get all users
     * GET /admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Update user status
     * PUT /admin/users/{userId}/status
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Integer userId,
            @RequestParam boolean isActive) {
        try {
            adminService.updateUserStatus(userId, isActive);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User status updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Get all movies
     * GET /admin/movies
     */
    @GetMapping("/movies")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = adminService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    /**
     * Get popular movies
     * GET /admin/movies/popular
     */
    @GetMapping("/movies/popular")
    public ResponseEntity<List<Movie>> getPopularMovies(
            @RequestParam(defaultValue = "10") int limit) {
        List<Movie> movies = adminService.getPopularMovies(limit);
        return ResponseEntity.ok(movies);
    }

    /**
     * Delete movie
     * DELETE /admin/movies/{movieId}
     */
    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<?> deleteMovie(@PathVariable Integer movieId) {
        try {
            adminService.deleteMovie(movieId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Movie deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Get all theaters
     * GET /admin/theaters
     */
    @GetMapping("/theaters")
    public ResponseEntity<List<Theater>> getAllTheaters() {
        List<Theater> theaters = adminService.getAllTheaters();
        return ResponseEntity.ok(theaters);
    }

    /**
     * Delete theater
     * DELETE /admin/theaters/{theaterId}
     */
    @DeleteMapping("/theaters/{theaterId}")
    public ResponseEntity<?> deleteTheater(@PathVariable Integer theaterId) {
        try {
            adminService.deleteTheater(theaterId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Theater deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Get all shows
     * GET /admin/shows
     */
    @GetMapping("/shows")
    public ResponseEntity<List<Show>> getAllShows() {
        List<Show> shows = adminService.getAllShows();
        return ResponseEntity.ok(shows);
    }

    /**
     * Delete show
     * DELETE /admin/shows/{showId}
     */
    @DeleteMapping("/shows/{showId}")
    public ResponseEntity<?> deleteShow(@PathVariable Integer showId) {
        try {
            adminService.deleteShow(showId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Show deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
