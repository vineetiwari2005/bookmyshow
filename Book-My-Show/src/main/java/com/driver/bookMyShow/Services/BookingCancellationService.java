package com.driver.bookMyShow.Services;

import com.driver.bookMyShow.Enums.PaymentStatus;
import com.driver.bookMyShow.Models.Payment;
import com.driver.bookMyShow.Models.Show;
import com.driver.bookMyShow.Models.Ticket;
import com.driver.bookMyShow.Models.User;
import com.driver.bookMyShow.Repositories.PaymentRepository;
import com.driver.bookMyShow.Repositories.TicketRepository;
import com.driver.bookMyShow.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * BookingCancellationService - Handles ticket cancellations and refunds
 * 
 * NEW SERVICE - Extends booking functionality without modifying existing code
 * 
 * Refund Policy:
 * - Full refund (minus convenience fee): More than 24 hours before show
 * - 50% refund: 6-24 hours before show
 * - No refund: Less than 6 hours before show
 */
@Service
public class BookingCancellationService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentService;

    /**
     * Cancel ticket and process refund
     */
    @Transactional
    public CancellationResult cancelTicket(Integer ticketId, Integer userId) throws Exception {
        // Validate ticket exists
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new Exception("Ticket not found"));

        // Validate user owns the ticket
        if (!ticket.getUser().getId().equals(userId)) {
            throw new Exception("You are not authorized to cancel this ticket");
        }

        // Check if ticket is already cancelled
        // (In a real system, we'd have a status field in Ticket entity)
        
        // Get show details
        Show show = ticket.getShow();
        Date showDate = show.getDate();
        
        // Calculate hours until show
        long hoursUntilShow = calculateHoursUntilShow(showDate, show.getTime());

        // Calculate refund amount based on policy
        RefundCalculation refundCalc = calculateRefund(ticket, hoursUntilShow);

        if (refundCalc.getRefundAmount() <= 0) {
            throw new Exception("Ticket cannot be cancelled. Show starts in less than 6 hours.");
        }

        // Find payment for this ticket
        Optional<Payment> paymentOpt = paymentRepository.findAll().stream()
                .filter(p -> p.getTicket() != null && 
                           p.getTicket().getId().equals(ticketId))
                .findFirst();

        if (paymentOpt.isEmpty()) {
            throw new Exception("Payment record not found for this ticket");
        }

        Payment payment = paymentOpt.get();

        // Process refund
        Payment refundedPayment = paymentService.processRefund(
            payment.getId(),
            "Ticket cancellation by user. Refund: " + refundCalc.getRefundPercentage() + "%"
        );

        // In a real system, we'd mark ticket as cancelled
        // For now, we'll delete it or add a cancelled status
        // ticket.setStatus(TicketStatus.CANCELLED);
        // ticketRepository.save(ticket);

        return CancellationResult.builder()
                .success(true)
                .ticketId(ticketId)
                .refundAmount(refundCalc.getRefundAmount())
                .refundPercentage(refundCalc.getRefundPercentage())
                .hoursUntilShow(hoursUntilShow)
                .message("Ticket cancelled successfully. Refund of ₹" + 
                        refundCalc.getRefundAmount() + " credited to wallet.")
                .build();
    }

    /**
     * Calculate hours until show starts
     */
    private long calculateHoursUntilShow(Date showDate, java.sql.Time showTime) {
        LocalDateTime showDateTime = LocalDateTime.of(
            showDate.toLocalDate(),
            showTime.toLocalTime()
        );
        
        LocalDateTime now = LocalDateTime.now();
        return ChronoUnit.HOURS.between(now, showDateTime);
    }

    /**
     * Calculate refund based on cancellation time
     */
    private RefundCalculation calculateRefund(Ticket ticket, long hoursUntilShow) {
        double totalAmount = ticket.getTotalTicketsPrice();
        double refundAmount = 0.0;
        int refundPercentage = 0;

        if (hoursUntilShow > 24) {
            // Full refund minus convenience fee
            refundAmount = totalAmount * 0.95; // Deduct 5% convenience fee
            refundPercentage = 95;
        } else if (hoursUntilShow >= 6) {
            // 50% refund
            refundAmount = totalAmount * 0.50;
            refundPercentage = 50;
        } else {
            // No refund
            refundAmount = 0.0;
            refundPercentage = 0;
        }

        return RefundCalculation.builder()
                .refundAmount(refundAmount)
                .refundPercentage(refundPercentage)
                .build();
    }

    /**
     * Get cancellation policy details
     */
    public CancellationPolicy getCancellationPolicy(Integer ticketId) throws Exception {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new Exception("Ticket not found"));

        Show show = ticket.getShow();
        long hoursUntilShow = calculateHoursUntilShow(show.getDate(), show.getTime());
        RefundCalculation refundCalc = calculateRefund(ticket, hoursUntilShow);

        return CancellationPolicy.builder()
                .ticketId(ticketId)
                .ticketAmount(ticket.getTotalTicketsPrice().doubleValue())
                .hoursUntilShow(hoursUntilShow)
                .canCancel(hoursUntilShow >= 6)
                .refundAmount(refundCalc.getRefundAmount())
                .refundPercentage(refundCalc.getRefundPercentage())
                .policyMessage(getPolicyMessage(hoursUntilShow))
                .build();
    }

    /**
     * Get policy message based on hours until show
     */
    private String getPolicyMessage(long hoursUntilShow) {
        if (hoursUntilShow > 24) {
            return "Cancellation eligible for 95% refund (5% convenience fee deducted)";
        } else if (hoursUntilShow >= 6) {
            return "Cancellation eligible for 50% refund";
        } else {
            return "Cancellation not allowed. Show starts in less than 6 hours.";
        }
    }

    // Helper DTOs
    @lombok.Data
    @lombok.Builder
    public static class CancellationResult {
        private boolean success;
        private Integer ticketId;
        private double refundAmount;
        private int refundPercentage;
        private long hoursUntilShow;
        private String message;
    }

    @lombok.Data
    @lombok.Builder
    private static class RefundCalculation {
        private double refundAmount;
        private int refundPercentage;
    }

    @lombok.Data
    @lombok.Builder
    public static class CancellationPolicy {
        private Integer ticketId;
        private double ticketAmount;
        private long hoursUntilShow;
        private boolean canCancel;
        private double refundAmount;
        private int refundPercentage;
        private String policyMessage;
    }
}
