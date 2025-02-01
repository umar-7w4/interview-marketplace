package com.mockxpert.interview_marketplace.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;


@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;
    
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;  

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Automatically create an interview when linked
    @JoinColumn(name = "interview_id", referencedColumnName = "interview_id", nullable = true)
    private Interview interview;
    
    public Long getPaymentId() {
		return paymentId;
	}


	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}


	public Booking getBooking() {
		return booking;
	}


	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public String getTransactionId() {
		return transactionId;
	}


	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}


	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}


	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}


	public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getPaymentMethod() {
		return paymentMethod;
	}


	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}


	public String getReceiptUrl() {
		return receiptUrl;
	}


	public void setReceiptUrl(String receiptUrl) {
		this.receiptUrl = receiptUrl;
	}


	public BigDecimal getRefundAmount() {
		return refundAmount;
	}


	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}


	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}


	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}


	public Interview getInterview() {
		return interview;
	}


	public void setInterview(Interview interview) {
		this.interview = interview;
	}

    
    public enum PaymentStatus {
        PAID,
        FAILED,
        REFUNDED
    }
    
    
    
}
