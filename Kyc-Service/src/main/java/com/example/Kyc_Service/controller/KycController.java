package com.example.Kyc_Service.controller;

import com.example.Kyc_Service.dto.*;
import com.example.Kyc_Service.enums.DocumentType;
import com.example.Kyc_Service.service.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<KycResponseDto>> startKyc(
            @Valid @RequestBody StartKycRequestDto request) {
        KycResponseDto kycResponseDto = kycService.startKyc(request.getUserId());

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "KYC Started Successfully",
                        kycResponseDto,
                        200
                )
        );
    }

    @GetMapping("/status/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<KycStatusResponseDto>> getStatus(
            @PathVariable Long userId) {
        KycStatusResponseDto kycStatusResponseDto = kycService.getKycStatus(userId);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "KYC Status Fetched Successfully",
                        kycStatusResponseDto,
                        200
                )
        );
    }

    @PostMapping("/verify/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<KycVerificationResponseDto>> verifyKyc(
            @PathVariable Long userId) {

        KycVerificationResponseDto result = kycService.verifyKyc(userId);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "KYC Verification Result",
                        result,
                        200
                )
        );
    }

    @PostMapping("/upload/pan")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<UploadDocumentResponseDto>> uploadPan(
            @RequestParam Long userId,
            @RequestParam MultipartFile file) {
        UploadDocumentResponseDto uploadDocumentResponseDto =  kycService.uploadDocument(
                                                              userId,
                                                                file,
                                                              DocumentType.PAN);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "PAN Uploaded Successfully",
                        uploadDocumentResponseDto,
                        200
                )
        );
    }

    @PostMapping("/upload/aadhaar")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<UploadDocumentResponseDto>> uploadAadhaar(
            @RequestParam Long userId,
            @RequestParam MultipartFile file) {

        UploadDocumentResponseDto uploadDocumentResponseDto =  kycService.uploadDocument(
                userId,
                file,
                DocumentType.AADHAAR);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "AADHAAR Uploaded Successfully",
                        uploadDocumentResponseDto,
                        200
                )
        );
    }

    @GetMapping("/ocr/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<?>> getOcrText(
            @PathVariable Long documentId) {

        String res =  kycService.getOcrText(documentId);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "OCR Text",
                        res,
                        200
                )
        );
    }

    @GetMapping("/document/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<DocumentDetailsResponseDto>>
    getDocumentDetails(
            @PathVariable Long documentId) {
        DocumentDetailsResponseDto responseDto = kycService.getDocumentDetails(
                                                                         documentId);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Document Details",
                        responseDto,
                        200
                )
        );
    }



    @PostMapping("/voice/upload")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<VoiceVerificationResponseDto>> uploadVoice(
            @RequestParam Long userId,
            @RequestParam MultipartFile file) {

        VoiceVerificationResponseDto response =
                kycService.uploadVoice(
                        userId,
                        file);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Voice Uploaded Successfully",
                        response,
                        200
                )
        );
    }

    @PostMapping("/voice/verify/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<VoiceVerificationResultDto>> verifyVoice(
            @PathVariable Long userId) {

        VoiceVerificationResultDto result =
                kycService.verifyVoice(
                        userId);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Voice Verification Result",
                        result,
                        200
                )
        );
    }
}