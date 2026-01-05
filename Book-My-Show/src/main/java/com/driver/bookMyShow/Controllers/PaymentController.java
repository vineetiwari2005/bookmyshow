package com.driver.bookMyShow.Controllers;

import com.driver.bookMyShow.Dtos.RequestDtos.PaymentInitiationDto;
import com.driver.bookMyShow.Dtos.ResponseDtos.PaymentResponseDto;
import com.driver.bookMyShow.Models.Payment;
import com.driver.bookMyShow.Services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * PaymentController - Handles payment operations
 * 
 * NEW ENDPOINTS for payment processing:
 * - Initiate payment
 * - Process payment
 * - Check payment status
 * - Process refund
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Initiate payment
     * POST /api/payment/initiate
     * 
     * Creates payment record with price breakdown
     * Returns transaction ID for payment processing
     */
    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@Valid @RequestBody PaymentInitiationDto request) {
        try {
            Payment payment = paymentService.initiatePayment(
                    request.getSessionId(),
                    request.getUserId(),
                    request.getBaseAmount(),
                    request.getPaymentMethod(),
                    request.getPromoCode()
            );

            PaymentResponseDto response = buildPaymentResponse(payment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Process payment
     * POST /api/payment/process/{transactionId}
     * 
     * Processes payment through gateway
     * Confirms or releases seat locks based on result
     */
    @PostMapping("/process/{transactionId}")
    public ResponseEntity<?> processPayment(@PathVariable String transactionId) {
        try {
            Payment payment = paymentService.processPayment(transactionId);
            PaymentResponseDto response = buildPaymentResponse(payment);
            
            if (payment.getStatus().name().equals("SUCCESS")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Get payment status
     * GET /api/payment/status/{transactionId}
     * 
     * Retrieves payment details and status
     */
    @GetMapping("/status/{transactionId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String transactionId) {
        try {
            Payment payment = paymentService.getPaymentByTransactionId(transactionId);
            PaymentResponseDto response = buildPaymentResponse(payment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Process refund
     * POST /api/payment/refund/{paymentId}
     * 
     * Initiates refund for a successful payment
     * Credits amount to user wallet
     */
    @PostMapping("/refund/{paymentId}")
    public ResponseEntity<?> processRefund(
            @PathVariable Integer paymentId,
            @RequestParam String reason) {
        try {
            Payment payment = paymentService.processRefund(paymentId, reason);
            PaymentResponseDto response = buildPaymentResponse(payment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Build payment response DTO
     */
    private PaymentResponseDto buildPaymentResponse(Payment payment) {
        String message;
        switch (payment.getStatus()) {
            case SUCCESS:
                message = "Payment completed successfully";
                break;
            case FAILED:
                message = "Payment failed: " + payment.getGatewayResponse();
                break;
            case PENDING:
                message = "Payment initiated. Please proceed to complete payment.";
                break;
            case PROCESSING:
                message = "Payment is being processed...";
                break;
            case REFUNDED:
                message = "Payment refunded successfully";
                break;
            default:
                message = "Payment status: " + payment.getStatus();
        }

        return PaymentResponseDto.builder()
                .paymentId(payment.getId())
                .transactionId(payment.getTransactionId())
                .sessionId(payment.getSessionId())
                .baseAmount(payment.getBaseAmount())
                .convenienceFee(payment.getConvenienceFee())
                .tax(payment.getTax())
                .discountAmount(payment.getDiscountAmount())
                .totalAmount(payment.getTotalAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .message(message)
                .createdAt(payment.getCreatedAt())
                .completedAt(payment.getCompletedAt())
                .build();
    }
}
