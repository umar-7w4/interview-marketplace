package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.services.PaymentService;
import com.mockxpert.interview_marketplace.dto.PaymentDto;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for handling Stripe webhook events.
 * Webhooks ensure that payments are securely verified before processing interview bookings.
 */
@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    @Autowired
    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    /**
     * Constructor-based dependency injection.
     *
     * @param paymentService Service for processing payment events.
     */
    public StripeWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Handles incoming Stripe webhook events.
     *
     * @param payload   The raw event payload from Stripe.
     * @param sigHeader The Stripe signature header for verification.
     * @return Response indicating the status of event processing.
     */
    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Verify webhook signature
            Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);

            logger.info("Received Stripe webhook event: {}", event.getType());

            if ("checkout.session.completed".equals(event.getType())) {
                // Extract session details
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session != null) {
                    String sessionId = session.getId();
                    logger.info("Processing successful payment for Session ID: {}", sessionId);

                    // Extract the OAuth Access Token stored in Stripe metadata
                    Map<String, String> metadata = session.getMetadata();
                    if (metadata == null || !metadata.containsKey("oauth_token")) {
                        logger.error("OAuth token missing in Stripe metadata for Session ID: {}", sessionId);
                        return ResponseEntity.badRequest().body("OAuth token missing in metadata.");
                    }

                    String oauthToken = metadata.get("oauth_token");

                    // Process the payment and create interview
                    PaymentDto payment = paymentService.processSuccessfulPayment(sessionId);

                    logger.info("Interview scheduled for Booking ID: {}", payment.getBookingId());
                } else {
                    logger.warn("Stripe session is null for event type: {}", event.getType());
                }
            } else if ("payment_intent.payment_failed".equals(event.getType())) {
                logger.error("Payment failed for a session.");
            }

            return ResponseEntity.ok("Webhook processed successfully.");

        } catch (SignatureVerificationException e) {
            logger.error("Invalid Stripe Webhook Signature!", e);
            return ResponseEntity.badRequest().body("Invalid webhook signature.");
        } catch (StripeException e) {
            logger.error("Stripe API error while processing webhook.", e);
            return ResponseEntity.internalServerError().body("Stripe processing error.");
        } catch (Exception e) {
            logger.error("Unexpected error processing webhook.", e);
            return ResponseEntity.internalServerError().body("Error processing webhook.");
        }
    }
}
