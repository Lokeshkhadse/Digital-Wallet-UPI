package com.example.Kyc_Service.dto;


import com.example.Kyc_Service.enums.DocumentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DocumentDetailsResponseDto {

    private Long documentId;

    private DocumentType documentType;

    private String name;

    private String dob;

    private String documentNumber;
}
