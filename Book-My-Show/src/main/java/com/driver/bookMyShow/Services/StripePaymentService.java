package com.driver.bookMyShow.Services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * StripePaymentService - Handles Stripe payment processing
 * 
 * Features:
 * - Create payment intent
 * - Retrieve payment status
 * - Confirm payment
 * 
 * NOTE: This uses Stripe TEST MODE (free) with test API keys
 * Test card: 4242 4242 4242 4242, any future expiry, any CVC
 */
@Service
public class StripePaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Create a payment intent with Stripe
     * @param amount Amount in smallest currency unit (paise for INR, cents for USD)
     * @param currency Currency code (inr, usd, etc.)
     * @return PaymentIntent with client secret
     */
    public PaymentIntent createPaymentIntent(Long amount, String currency) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .addPaymentMethodType("card")
                .build();

        return PaymentIntent.create(params);
    }

    /**
     * Create payment intent with metadata
     */
    public PaymentIntent createPaymentIntent(Long amount, String currency, Map<String, String> metadata) 
            throws StripeException {
        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .addPaymentMethodType("card");

        if (metadata != null && !metadata.isEmpty()) {
            paramsBuilder.putAllMetadata(metadata);
        }

        return PaymentIntent.create(paramsBuilder.build());
    }

    /**
     * Retrieve payment intent by ID
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }

    /**
     * Cancel payment intent
     */
    public PaymentIntent cancelPaymentIntent(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return paymentIntent.cancel();
    }
}
