package com.driver.bookMyShow.Services;

import com.driver.bookMyShow.Dtos.RequestDtos.MovieEntryDto;
import com.driver.bookMyShow.Dtos.RequestDtos.ShowEntryDto;
import com.driver.bookMyShow.Dtos.RequestDtos.TheaterEntryDto;
import com.driver.bookMyShow.Enums.PaymentStatus;
import com.driver.bookMyShow.Models.Movie;
import com.driver.bookMyShow.Models.Show;
import com.driver.bookMyShow.Models.Theater;
import com.driver.bookMyShow.Repositories.*;
import com.driver.bookMyShow.Transformers.MovieTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminService - Provides administrative operations
 * 
 * Features:
 * - Movie management (CRUD)
 * - Theater management
 * - Show management
 * - Analytics and reporting
 * - User management
 */
@Service
public class AdminService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Get comprehensive analytics dashboard data
     */
    public Map<String, Object> getDashboardAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        // User statistics
        long totalUsers = userRepository.count();
        analytics.put("totalUsers", totalUsers);

        // Movie statistics
        long totalMovies = movieRepository.count();
        analytics.put("totalMovies", totalMovies);

        // Theater statistics
        long totalTheaters = theaterRepository.count();
        analytics.put("totalTheaters", totalTheaters);

        // Show statistics
        long totalShows = showRepository.count();
        analytics.put("totalShows", totalShows);

        // Ticket statistics
        long totalTickets = ticketRepository.count();
        analytics.put("totalTicketsBooked", totalTickets);

        // Revenue statistics
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime monthEnd = LocalDateTime.now();
        
        Double monthlyRevenue = paymentRepository.getTotalRevenue(monthStart, monthEnd);
        analytics.put("monthlyRevenue", monthlyRevenue != null ? monthlyRevenue : 0.0);

        // Payment statistics
        long successfulPayments = paymentRepository.findByStatus(PaymentStatus.SUCCESS).size();
        long failedPayments = paymentRepository.findByStatus(PaymentStatus.FAILED).size();
        
        analytics.put("successfulPayments", successfulPayments);
        analytics.put("failedPayments", failedPayments);

        return analytics;
    }

    /**
     * Get revenue report for date range
     */
    public Map<String, Object> getRevenueReport(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> report = new HashMap<>();

        Double totalRevenue = paymentRepository.getTotalRevenue(startDate, endDate);
        report.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        report.put("startDate", startDate);
        report.put("endDate", endDate);

        List<com.driver.bookMyShow.Models.Payment> successfulPayments = 
            paymentRepository.findByStatus(PaymentStatus.SUCCESS);
        
        report.put("transactionCount", successfulPayments.size());

        return report;
    }

    /**
     * Get all users (paginated in real implementation)
     */
    public List<com.driver.bookMyShow.Models.User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get all movies
     */
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    /**
     * Get all theaters
     */
    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    /**
     * Get all shows
     */
    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    /**
     * Delete movie (admin only)
     */
    @Transactional
    public void deleteMovie(Integer movieId) throws Exception {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new Exception("Movie not found"));
        
        // Check if movie has active shows
        if (!movie.getShows().isEmpty()) {
            throw new Exception("Cannot delete movie with active shows. Please remove shows first.");
        }

        movieRepository.delete(movie);
    }

    /**
     * Delete theater (admin only)
     */
    @Transactional
    public void deleteTheater(Integer theaterId) throws Exception {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new Exception("Theater not found"));
        
        // Check if theater has active shows
        if (!theater.getShowList().isEmpty()) {
            throw new Exception("Cannot delete theater with active shows. Please remove shows first.");
        }

        theaterRepository.delete(theater);
    }

    /**
     * Delete show (admin only)
     */
    @Transactional
    public void deleteShow(Integer showId) throws Exception {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new Exception("Show not found"));
        
        // Check if show has bookings
        if (!show.getTicketList().isEmpty()) {
            throw new Exception("Cannot delete show with existing bookings.");
        }

        showRepository.delete(show);
    }

    /**
     * Update user status (activate/deactivate)
     */
    @Transactional
    public void updateUserStatus(Integer userId, boolean isActive) throws Exception {
        com.driver.bookMyShow.Models.User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        
        user.setIsActive(isActive);
        userRepository.save(user);
    }

    /**
     * Get popular movies (by booking count)
     */
    public List<Movie> getPopularMovies(int limit) {
        // In real implementation, use a query to sort by ticket count
        List<Movie> allMovies = movieRepository.findAll();
        allMovies.sort((m1, m2) -> {
            int count1 = m1.getShows().stream()
                    .mapToInt(show -> show.getTicketList().size())
                    .sum();
            int count2 = m2.getShows().stream()
                    .mapToInt(show -> show.getTicketList().size())
                    .sum();
            return Integer.compare(count2, count1);
        });
        
        return allMovies.stream()
                .limit(limit)
                .toList();
    }
}
