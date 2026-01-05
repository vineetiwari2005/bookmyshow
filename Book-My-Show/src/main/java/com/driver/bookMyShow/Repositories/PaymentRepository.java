package com.driver.bookMyShow.Repositories;

import com.driver.bookMyShow.Enums.PaymentStatus;
import com.driver.bookMyShow.Models.Payment;
import com.driver.bookMyShow.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * PaymentRepository - Data access for payment operations
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    /**
     * Find payment by transaction ID (for idempotency)
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Find payment by session ID
     */
    Optional<Payment> findBySessionId(String sessionId);

    /**
     * Find all payments by user
     */
    List<Payment> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Find payments by status
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Find pending payments older than specified time
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' " +
           "AND p.createdAt < :cutoffTime")
    List<Payment> findStalePendingPayments(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Get total revenue between dates
     */
    @Query("SELECT SUM(p.totalAmount) FROM Payment p " +
           "WHERE p.status = 'SUCCESS' " +
           "AND p.completedAt BETWEEN :startDate AND :endDate")
    Double getTotalRevenue(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);

    /**
     * Get successful payments count for a user
     */
    @Query("SELECT COUNT(p) FROM Payment p " +
           "WHERE p.user.id = :userId AND p.status = 'SUCCESS'")
    Long countSuccessfulPaymentsByUser(@Param("userId") Integer userId);

    /**
     * Find payment by gateway transaction ID
     */
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
}
