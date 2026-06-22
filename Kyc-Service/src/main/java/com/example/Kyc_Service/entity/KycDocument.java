package com.example.Kyc_Service.entity;


import com.example.Kyc_Service.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kyc_id", nullable = false)
    private Long kycId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private DocumentType documentType;

    @Lob
    @Column(name = "document_base64", columnDefinition = "LONGTEXT")
    private String documentBase64;

    @Lob
    @Column(name = "ocr_text", columnDefinition = "LONGTEXT")
    private String ocrText;

    @Column(name = "extracted_name")
    private String extractedName;

    @Column(name = "extracted_dob")
    private String extractedDob;

    @Column(name = "extracted_document_number")
    private String extractedDocumentNumber;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;


}