package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.PaymentDto;
import com.mockxpert.interview_marketplace.entities.Booking;
import com.mockxpert.interview_marketplace.entities.Interview;
import com.mockxpert.interview_marketplace.entities.Payment;
import com.mockxpert.interview_marketplace.entities.User;

/**
 * Mapper class that converts Data Transfer Object to payment entity object.
 * 
 * @author Umar Mohammad
 */
public class PaymentMapper {
	
	/**
	 * Mapper converting Payment entity to DTO.
	 * 
	 * @param payment
	 * @return
	 */

    public static PaymentDto toDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(payment.getPaymentId());
        paymentDto.setBookingId(payment.getBooking() != null ? payment.getBooking().getBookingId() : null);
        paymentDto.setTransactionId(payment.getTransactionId());
        paymentDto.setPaymentDate(payment.getPaymentDate());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setCurrency(payment.getCurrency());
        paymentDto.setPaymentMethod(payment.getPaymentMethod());
        paymentDto.setReceiptUrl(payment.getReceiptUrl());
        paymentDto.setRefundAmount(payment.getRefundAmount());
        paymentDto.setPaymentStatus(payment.getPaymentStatus().name());
        paymentDto.setInterviewId(payment.getInterview() != null ? payment.getInterview().getInterviewId() : null);

        return paymentDto;
    }
    
    /**
     * Mapper converting Payment DTO to entity.
     * 
     * @param paymentDto
     * @param booking
     * @param interview
     * @return
     */

    public static Payment toEntity(PaymentDto paymentDto, Booking booking, Interview interview) {
        if (paymentDto == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setPaymentId(paymentDto.getPaymentId());
        payment.setBooking(booking);
        payment.setTransactionId(paymentDto.getTransactionId());
        payment.setPaymentDate(paymentDto.getPaymentDate());
        payment.setAmount(paymentDto.getAmount());
        payment.setCurrency(paymentDto.getCurrency());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setReceiptUrl(paymentDto.getReceiptUrl());
        payment.setRefundAmount(paymentDto.getRefundAmount());
        payment.setPaymentStatus(Payment.PaymentStatus.valueOf(paymentDto.getPaymentStatus()));
        payment.setInterview(interview);

        return payment;
    }
}
