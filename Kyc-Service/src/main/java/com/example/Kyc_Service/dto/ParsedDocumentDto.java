package com.example.Kyc_Service.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParsedDocumentDto {

    private String name;

    private String dob;

    private String documentNumber;
}
