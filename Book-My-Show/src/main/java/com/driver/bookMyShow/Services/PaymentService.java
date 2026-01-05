package com.driver.bookMyShow.Services;

import com.driver.bookMyShow.Enums.PaymentMethod;
import com.driver.bookMyShow.Enums.PaymentStatus;
import com.driver.bookMyShow.Gateway.MockPaymentGateway;
import com.driver.bookMyShow.Models.Payment;
import com.driver.bookMyShow.Models.Ticket;
import com.driver.bookMyShow.Models.User;
import com.driver.bookMyShow.Repositories.PaymentRepository;
import com.driver.bookMyShow.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * PaymentService - Handles payment processing and lifecycle
 * 
 * Features:
 * - Idempotent payment initiation
 * - Integration with payment gateway
 * - Refund processing
 * - Price calculation with fees and taxes
 */
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockPaymentGateway paymentGateway;

    @Autowired
    private SeatLockService seatLockService;

    // Pricing constants
    private static final double CONVENIENCE_FEE_PERCENTAGE = 2.5; // 2.5%
    private static final double TAX_PERCENTAGE = 18.0; // 18% GST
    private static final double MIN_CONVENIENCE_FEE = 20.0;

    /**
     * Initiate payment (idempotent)
     * Creates or retrieves existing payment record
     */
    @Transactional
    public Payment initiatePayment(String sessionId, Integer userId, Double baseAmount, 
                                   PaymentMethod paymentMethod, String promoCode) throws Exception {
        
        // Check for existing payment with same session (idempotency)
        Optional<Payment> existingPayment = paymentRepository.findBySessionId(sessionId);
        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }

        // Verify seat locks are still active
        Long remainingTime = seatLockService.getRemainingTime(sessionId);
        if (remainingTime <= 0) {
            throw new Exception("Seat locks have expired. Please select seats again.");
        }

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // Calculate fees and taxes
        double convenienceFee = Math.max(
            baseAmount * CONVENIENCE_FEE_PERCENTAGE / 100,
            MIN_CONVENIENCE_FEE
        );
        double tax = (baseAmount + convenienceFee) * TAX_PERCENTAGE / 100;

        // Apply promo code discount if any
        double discountAmount = 0.0;
        if (promoCode != null && !promoCode.isEmpty()) {
            discountAmount = calculateDiscount(promoCode, baseAmount);
        }

        // Create payment record
        Payment payment = Payment.builder()
                .transactionId("TXN_" + UUID.randomUUID().toString().replace("-", ""))
                .sessionId(sessionId)
                .user(user)
                .baseAmount(baseAmount)
                .convenienceFee(convenienceFee)
                .tax(tax)
                .discountAmount(discountAmount)
                .promoCode(promoCode)
                .paymentMethod(paymentMethod)
                .status(PaymentStatus.PENDING)
                .build();

        payment.calculateTotal();
        return paymentRepository.save(payment);
    }

    /**
     * Process payment through gateway
     */
    @Transactional
    public Payment processPayment(String transactionId) throws Exception {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new Exception("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            // Already processed (idempotency)
            return payment;
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new Exception("Payment cannot be processed in current status: " + 
                              payment.getStatus());
        }

        // Verify seats are still locked
        Long remainingTime = seatLockService.getRemainingTime(payment.getSessionId());
        if (remainingTime <= 0) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setGatewayResponse("Seat locks expired");
            paymentRepository.save(payment);
            throw new Exception("Seat locks have expired");
        }

        // Update status to processing
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        try {
            // Call payment gateway
            MockPaymentGateway.PaymentGatewayResponse response = paymentGateway.processPayment(
                payment.getTotalAmount(),
                payment.getPaymentMethod().name(),
                payment.getUser().getEmailId()
            );

            payment.setGatewayTransactionId(response.getTransactionId());
            payment.setGatewayResponse(response.getMessage());

            if (response.isSuccess()) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setCompletedAt(LocalDateTime.now());
                
                // Confirm seat locks
                seatLockService.confirmLocks(payment.getSessionId());
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                
                // Release seat locks
                seatLockService.releaseLocks(payment.getSessionId());
            }

            return paymentRepository.save(payment);

        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setGatewayResponse("Error: " + e.getMessage());
            paymentRepository.save(payment);
            
            // Release seat locks
            seatLockService.releaseLocks(payment.getSessionId());
            
            throw new Exception("Payment processing failed: " + e.getMessage());
        }
    }

    /**
     * Process refund
     */
    @Transactional
    public Payment processRefund(Integer paymentId, String reason) throws Exception {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new Exception("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new Exception("Only successful payments can be refunded");
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            // Already refunded (idempotency)
            return payment;
        }

        // Process refund through gateway
        MockPaymentGateway.RefundResponse refundResponse = paymentGateway.processRefund(
            payment.getGatewayTransactionId(),
            payment.getTotalAmount()
        );

        if (refundResponse.isSuccess()) {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setRefundAmount(payment.getTotalAmount());
            payment.setRefundedAt(LocalDateTime.now());
            payment.setRefundReason(reason);

            // Credit to wallet
            User user = payment.getUser();
            user.setWalletBalance(user.getWalletBalance() + payment.getTotalAmount());
            userRepository.save(user);

            return paymentRepository.save(payment);
        } else {
            throw new Exception("Refund processing failed");
        }
    }

    /**
     * Calculate discount based on promo code
     * TODO: Implement promo code validation with database
     */
    private double calculateDiscount(String promoCode, double baseAmount) {
        // Mock implementation - replace with actual promo code logic
        if ("SAVE10".equalsIgnoreCase(promoCode)) {
            return baseAmount * 0.10; // 10% discount
        } else if ("SAVE20".equalsIgnoreCase(promoCode)) {
            return baseAmount * 0.20; // 20% discount
        } else if ("FIRSTBOOKING".equalsIgnoreCase(promoCode)) {
            return Math.min(baseAmount * 0.15, 100.0); // 15% up to ₹100
        }
        return 0.0;
    }

    /**
     * Get payment by transaction ID
     */
    public Payment getPaymentByTransactionId(String transactionId) throws Exception {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new Exception("Payment not found"));
    }

    /**
     * Get payment by session ID
     */
    public Payment getPaymentBySessionId(String sessionId) throws Exception {
        return paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new Exception("No payment found for this session"));
    }

    /**
     * Link ticket to payment after booking confirmation
     */
    @Transactional
    public void linkTicketToPayment(String transactionId, Ticket ticket) throws Exception {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new Exception("Payment not found"));

        payment.setTicket(ticket);
        paymentRepository.save(payment);
    }
}
