package com.driver.bookMyShow.Models;

import com.driver.bookMyShow.Enums.PaymentMethod;
import com.driver.bookMyShow.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Payment Entity - Tracks all payment transactions
 * 
 * Features:
 * - Idempotency support with unique transaction ID
 * - Complete payment lifecycle tracking
 * - Support for refunds and cancellations
 * - Price breakdown (base price + taxes + fees)
 */
@Entity
@Table(name = "PAYMENTS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String transactionId; // Unique ID for idempotency

    @Column(nullable = false)
    private String sessionId; // Links to seat lock session

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket; // Null until booking is confirmed

    @Column(nullable = false)
    private Double baseAmount; // Ticket price without fees

    @Column(nullable = false)
    private Double convenienceFee;

    @Column(nullable = false)
    private Double tax;

    @Column(nullable = false)
    private Double totalAmount;

    private Double discountAmount = 0.0;

    private String promoCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    // External payment gateway details
    private String gatewayTransactionId;
    
    private String gatewayResponse;

    // Refund details
    private Double refundAmount;
    
    private LocalDateTime refundedAt;
    
    private String refundReason;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    /**
     * Calculate total amount
     */
    public void calculateTotal() {
        this.totalAmount = this.baseAmount + this.convenienceFee + this.tax - this.discountAmount;
    }
}
