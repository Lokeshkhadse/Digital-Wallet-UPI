package com.example.Kyc_Service.dto;


import com.example.Kyc_Service.enums.DocumentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UploadDocumentResponseDto {

    private Long documentId;
    private Long kycId;
    private DocumentType documentType;
    private String message;
}
