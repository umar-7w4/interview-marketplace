package com.mockxpert.interview_marketplace.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "interviewer_verifications")
public class InterviewerVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long verificationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id", nullable = false)
    private Interviewer interviewer;

    @Column(name = "document_url", nullable = false)
    private String documentUrl;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verification_date")
    private LocalDate verificationDate;

    @Column(name = "verification_comments", length = 500)
    private String verificationComments;
    
    public enum VerificationStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

}
