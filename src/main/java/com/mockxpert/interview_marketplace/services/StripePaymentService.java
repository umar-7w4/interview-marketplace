package com.mockxpert.interview_marketplace.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * Service for handling Stripe payment integration.
 * This service is responsible for creating checkout sessions for interview bookings.
 * 
 * @author Umar Mohammad
 */
@Service
public class StripePaymentService {

    private static final Logger logger = LoggerFactory.getLogger(StripePaymentService.class);


    @Value("${stripe.secret.key}")
    private String secretKey;
    
    @Autowired
    private PaymentService paymentService;
 
    /**
     * Creates a Stripe checkout session for an interview booking.
     *
     * @param bookingId The ID of the booking associated with this payment.
     * @param amount    The amount to be paid (in USD).
     * @return The checkout URL that the user will be redirected to for payment.
     */
    public String createCheckoutSession(Long bookingId, double amount) {
        Stripe.apiKey = secretKey;

        try {
        	logger.info("Starting to make payment successful");
            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD) // Fix applied
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:8080/api/payments/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:8080/api/payments/cancel")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount((long) (amount * 100)) // Amount in cents
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Mock Interview Booking")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build(); 
  
            Session session = Session.create(params);
            paymentService.createPayment(bookingId, session.getId());
            logger.info("Stripe Checkout Session Created. Session ID: {}", session.getId());
            return session.getUrl(); 

        } catch (StripeException e) {
            logger.error("Error creating Stripe Checkout Session", e);
            throw new RuntimeException("Failed to create Stripe checkout session", e);
        }
    }
}
