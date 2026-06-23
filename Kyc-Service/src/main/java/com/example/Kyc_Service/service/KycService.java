//package com.example.Kyc_Service.service;
//
//
//import com.example.Kyc_Service.dto.*;
//import com.example.Kyc_Service.entity.KycDetails;
//import com.example.Kyc_Service.entity.KycDocument;
//import com.example.Kyc_Service.enums.DocumentType;
//import com.example.Kyc_Service.enums.KycStatus;
//import com.example.Kyc_Service.exception.KycException;
//import com.example.Kyc_Service.feign.AuthServiceClient;
//import com.example.Kyc_Service.repository.KycDetailsRepository;
//import com.example.Kyc_Service.repository.KycDocumentRepository;
//import com.example.Kyc_Service.util.FileUtil;
//import com.example.Kyc_Service.util.FuzzyMatchingUtil;
//import lombok.RequiredArgsConstructor;
//import org.apache.commons.text.similarity.LevenshteinDistance;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.time.LocalDateTime;
//import java.util.Objects;
//
//@Service
//@RequiredArgsConstructor
//public class  KycService {
//
//    private final KycDetailsRepository kycDetailsRepository;
//    private final KycDocumentRepository kycDocumentRepository;
//    private final OcrService ocrService;
//    private final DocumentParserService documentParserService;
//    private final AuthServiceClient authServiceClient;
//
//
//    @Transactional
//    public KycResponseDto startKyc(Long userId) {
//
//        if (kycDetailsRepository.findByUserId(userId).isPresent()) {
//            throw new KycException(
//                    "KYC already initiated for user : " + userId
//            );
//        }
//
//        KycDetails kycDetails = KycDetails.builder()
//                .userId(userId)
//                .status(KycStatus.PENDING)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        kycDetails = kycDetailsRepository.save(kycDetails);
//
//        return KycResponseDto.builder()
//                .kycId(kycDetails.getId())
//                .userId(userId)
//                .status(kycDetails.getStatus())
//                .build();
//    }
//
//
//    public KycStatusResponseDto getKycStatus(Long userId) {
//
//        KycDetails kycDetails = kycDetailsRepository
//                .findByUserId(userId)
//                .orElseThrow(() ->
//                        new KycException("KYC not found"));
//
//        return KycStatusResponseDto.builder()
//                .kycId(kycDetails.getId())
//                .userId(kycDetails.getUserId())
//                .status(kycDetails.getStatus())
//                .rejectionReason(kycDetails.getRejectionReason())
//                .build();
//    }
//
//    @Transactional
//    public UploadDocumentResponseDto uploadDocument(
//            Long userId,
//            MultipartFile file,
//            DocumentType documentType) {
//
//        try {
//
//            KycDetails kycDetails = kycDetailsRepository
//                    .findByUserId(userId)
//                    .orElseThrow(() ->
//                            new KycException("KYC not found"));
//
//            String base64 =
//                    FileUtil.convertToBase64(file);
//
//            String ocrText =
//                    ocrService.extractTextFromBase64(base64);
//
//            ParsedDocumentDto parsedDocument =
//                    documentParserService.parseDocument(
//                            ocrText,
//                            documentType
//                    );
//
//            KycDocument document =
//                    KycDocument.builder()
//                            .kycId(kycDetails.getId())
//                            .documentType(documentType)
//                            .documentBase64(base64)
//                            .ocrText(ocrText)
//                            .extractedName(
//                                    parsedDocument.getName()
//                            )
//                            .extractedDob(
//                                    parsedDocument.getDob()
//                            )
//                            .extractedDocumentNumber(
//                                    parsedDocument.getDocumentNumber()
//                            )
//                            .uploadedAt(LocalDateTime.now())
//                            .build();
//
//            document = kycDocumentRepository.save(document);
//
//            return UploadDocumentResponseDto.builder()
//                    .documentId(document.getId())
//                    .kycId(kycDetails.getId())
//                    .documentType(documentType)
//                    .message("Document uploaded successfully")
//                    .build();
//
//        } catch (Exception ex) {
//            throw new KycException(ex.getMessage());
//        }
//    }
//
//    public String getOcrText(Long documentId) {
//
//        KycDocument document =
//                kycDocumentRepository
//                        .findById(documentId)
//                        .orElseThrow(
//                                () -> new KycException(
//                                        "Document not found"));
//
//        return document.getOcrText();
//    }
//
//    public DocumentDetailsResponseDto getDocumentDetails(Long documentId) {
//
//        KycDocument document =
//                kycDocumentRepository
//                        .findById(documentId)
//                        .orElseThrow(() ->
//                                new KycException(
//                                        "Document not found"));
//
//        return DocumentDetailsResponseDto
//                .builder()
//                .documentId(document.getId())
//                .documentType(
//                        document.getDocumentType())
//                .name(
//                        document.getExtractedName())
//                .dob(
//                        document.getExtractedDob())
//                .documentNumber(
//                        document.getExtractedDocumentNumber())
//                .build();
//    }
//
//    @Transactional
//    public KycVerificationResponseDto verifyKyc(Long userId) {
//
//        KycDetails kycDetails =
//                kycDetailsRepository
//                        .findByUserId(userId)
//                        .orElseThrow(() ->
//                                new KycException("KYC not found"));
//
//        KycDocument panDocument =
//                kycDocumentRepository
//                        .findByKycIdAndDocumentType(
//                                kycDetails.getId(),
//                                DocumentType.PAN)
//                        .orElseThrow(() ->
//                                new KycException(
//                                        "PAN document not found"));
//
//        KycDocument aadhaarDocument =
//                kycDocumentRepository
//                        .findByKycIdAndDocumentType(
//                                kycDetails.getId(),
//                                DocumentType.AADHAAR)
//                        .orElseThrow(() ->
//                                new KycException(
//                                        "AADHAAR document not found"));
//
//        UserResponseDto user =
//                authServiceClient.getUserById(userId);
//
//        // USER vs PAN
//        double panSimilarity =
//                FuzzyMatchingUtil.calculateSimilarity(
//                        user.getName(),
//                        panDocument.getExtractedName());
//
//        boolean panDobMatched =
//                Objects.equals(
//                        user.getDob(),
//                        panDocument.getExtractedDob());
//
//        // USER vs AADHAAR
//        double aadhaarSimilarity =
//                FuzzyMatchingUtil.calculateSimilarity(
//                        user.getName(),
//                        aadhaarDocument.getExtractedName());
//
//        boolean aadhaarDobMatched =
//                Objects.equals(
//                        user.getDob(),
//                        aadhaarDocument.getExtractedDob());
//
//        // PAN vs AADHAAR
//        double documentSimilarity =
//                FuzzyMatchingUtil.calculateSimilarity(
//                        panDocument.getExtractedName(),
//                        aadhaarDocument.getExtractedName());
//
//        boolean documentDobMatched =
//                Objects.equals(
//                        panDocument.getExtractedDob(),
//                        aadhaarDocument.getExtractedDob());
//
//        String finalStatus;
//        String remarks;
//
//        if (panSimilarity >= 90
//                && aadhaarSimilarity >= 90
//                && panDobMatched
//                && aadhaarDobMatched
//                && documentSimilarity >= 90
//                && documentDobMatched) {
//
//            finalStatus = "APPROVED";
//
//            remarks =
//                    "PAN and Aadhaar verified successfully";
//
//           kycDetails.setStatus(
//                   KycStatus.APPROVED);

//
//            kycDetails.setVerifiedAt(
//                    LocalDateTime.now());
//
//        }
//        else if (panSimilarity >= 75
//                || aadhaarSimilarity >= 75) {
//
//            finalStatus = "UNDER_REVIEW";
//
//            remarks =
//                    "Manual verification required";
//
//            kycDetails.setStatus(
//                    KycStatus.UNDER_REVIEW);
//
//        }
//        else {
//
//            finalStatus = "REJECTED";
//
//            remarks =
//                    "Document verification failed";
//
//            kycDetails.setStatus(
//                    KycStatus.REJECTED);
//
//            kycDetails.setRejectionReason(
//                    remarks);
//        }
//
//        kycDetails.setUpdatedAt(
//                LocalDateTime.now());
//
//        kycDetailsRepository.save(
//                kycDetails);
//
//        return KycVerificationResponseDto
//                .builder()
//                .userId(userId)
//                .panMatchPercentage(
//                        panSimilarity)
//                .aadhaarMatchPercentage(
//                        aadhaarSimilarity)
//                .panAadhaarMatchPercentage(
//                        documentSimilarity)
//                .panDobMatched(
//                        panDobMatched)
//                .aadhaarDobMatched(
//                        aadhaarDobMatched)
//                .panAadhaarDobMatched(
//                        documentDobMatched)
//                .finalStatus(
//                        finalStatus)
//                .remarks(
//                        remarks)
//                .build();
//    }
//}


package com.example.Kyc_Service.service;


import com.example.Kyc_Service.dto.*;
import com.example.Kyc_Service.entity.KycDetails;
import com.example.Kyc_Service.entity.KycDocument;
import com.example.Kyc_Service.entity.KycVoiceVerification;
import com.example.Kyc_Service.enums.DocumentType;
import com.example.Kyc_Service.enums.KycStatus;
import com.example.Kyc_Service.exception.KycException;
import com.example.Kyc_Service.feign.AuthServiceClient;
import com.example.Kyc_Service.repository.KycDetailsRepository;
import com.example.Kyc_Service.repository.KycDocumentRepository;
import com.example.Kyc_Service.repository.KycVoiceVerificationRepository;
import com.example.Kyc_Service.util.AudioUtil;
import com.example.Kyc_Service.util.FileUtil;
import com.example.Kyc_Service.util.FuzzyMatchingUtil;
import com.example.Kyc_Service.util.VoiceFileUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class  KycService {

    private final KycDetailsRepository kycDetailsRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private final OcrService ocrService;
    private final DocumentParserService documentParserService;
    private final AuthServiceClient authServiceClient;
    private final KycVoiceVerificationRepository kycVoiceVerificationRepository;
   // private final SpeechToTextService speechToTextService;
    private final VoiceParserService voiceParserService;
    private final SarvamSpeechToTextService sarvamSpeechToTextService;


    @Transactional
    public KycResponseDto startKyc(Long userId) {

        if (kycDetailsRepository.findByUserId(userId).isPresent()) {
            throw new KycException(
                    "KYC already initiated for user : " + userId
            );
        }

        KycDetails kycDetails = KycDetails.builder()
                .userId(userId)
                .status(KycStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        kycDetails = kycDetailsRepository.save(kycDetails);

        return KycResponseDto.builder()
                .kycId(kycDetails.getId())
                .userId(userId)
                .status(kycDetails.getStatus())
                .build();
    }


    public KycStatusResponseDto getKycStatus(Long userId) {

        KycDetails kycDetails = kycDetailsRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new KycException("KYC not found"));

        return KycStatusResponseDto.builder()
                .kycId(kycDetails.getId())
                .userId(kycDetails.getUserId())
                .status(kycDetails.getStatus())
                .rejectionReason(kycDetails.getRejectionReason())
                .build();
    }

    @Transactional
    public UploadDocumentResponseDto uploadDocument(
            Long userId,
            MultipartFile file,
            DocumentType documentType) {

        try {



            KycDetails kycDetails = kycDetailsRepository
                    .findByUserId(userId)
                    .orElseThrow(() ->
                            new KycException("KYC not found"));

            kycDocumentRepository
                    .findByKycIdAndDocumentType(
                            kycDetails.getId(),
                            documentType
                    )
                    .ifPresent(doc -> {
                        throw new KycException(
                                documentType + " already uploaded"
                        );
                    });

            String base64 =
                    FileUtil.convertToBase64(file);

            String ocrText =
                    ocrService.extractTextFromBase64(base64);

            ParsedDocumentDto parsedDocument =
                    documentParserService.parseDocument(
                            ocrText,
                            documentType
                    );

            KycDocument document =
                    KycDocument.builder()
                            .kycId(kycDetails.getId())
                            .documentType(documentType)
                            .documentBase64(base64)
                            .ocrText(ocrText)
                            .extractedName(
                                    parsedDocument.getName()
                            )
                            .extractedDob(
                                    parsedDocument.getDob()
                            )
                            .extractedDocumentNumber(
                                    parsedDocument.getDocumentNumber()
                            )
                            .uploadedAt(LocalDateTime.now())
                            .build();

            document = kycDocumentRepository.save(document);
            kycDetails.setStatus(
                    KycStatus.DOCUMENT_UPLOADED
            );
            kycDetails.setUpdatedAt(
                    LocalDateTime.now()
            );

            kycDetailsRepository.save(
                    kycDetails
            );



            return UploadDocumentResponseDto.builder()
                    .documentId(document.getId())
                    .kycId(kycDetails.getId())
                    .documentType(documentType)
                    .message("Document uploaded successfully")
                    .build();

        } catch (Exception ex) {
            throw new KycException(ex.getMessage());
        }
    }

    public String getOcrText(Long documentId) {

        KycDocument document =
                kycDocumentRepository
                        .findById(documentId)
                        .orElseThrow(
                                () -> new KycException(
                                        "Document not found"));

        return document.getOcrText();
    }

    public DocumentDetailsResponseDto getDocumentDetails(Long documentId) {

        KycDocument document =
                kycDocumentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new KycException(
                                        "Document not found"));

        return DocumentDetailsResponseDto
                .builder()
                .documentId(document.getId())
                .documentType(
                        document.getDocumentType())
                .name(
                        document.getExtractedName())
                .dob(
                        document.getExtractedDob())
                .documentNumber(
                        document.getExtractedDocumentNumber())
                .build();
    }

    @Transactional
    public KycVerificationResponseDto verifyKyc(Long userId) {

        KycDetails kycDetails =
                kycDetailsRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new KycException("KYC not found"));

        if (kycDetails.getStatus()
                == KycStatus.REJECTED) {

            throw new KycException(
                    "KYC already rejected"
            );
        }

//        if (kycDetails.getStatus()
//                != KycStatus.DOCUMENT_UPLOADED) {
//
//            throw new KycException(
//                    "Please upload PAN and Aadhaar first"
//            );
//        }
        KycDocument panDocument =
                kycDocumentRepository
                        .findByKycIdAndDocumentType(
                                kycDetails.getId(),
                                DocumentType.PAN)
                        .orElseThrow(() ->
                                new KycException(
                                        "PAN document not found"));
        System.out.println("pan card dob " +panDocument.getExtractedDob());

        KycDocument aadhaarDocument =
                kycDocumentRepository
                        .findByKycIdAndDocumentType(
                                kycDetails.getId(),
                                DocumentType.AADHAAR)
                        .orElseThrow(() ->
                                new KycException(
                                        "AADHAAR document not found"));

        System.out.println("aadhaar card dob " +aadhaarDocument.getExtractedDob());


        UserResponseDto user =
                authServiceClient.getUserById(userId);

        // USER vs PAN
        double panSimilarity =
                FuzzyMatchingUtil.calculateSimilarity(
                        user.getName(),
                        panDocument.getExtractedName());

        boolean panDobMatched =
                Objects.equals(
                        user.getDob(),
                        panDocument.getExtractedDob());

        // USER vs AADHAAR
        double aadhaarSimilarity =
                FuzzyMatchingUtil.calculateSimilarity(
                        user.getName(),
                        aadhaarDocument.getExtractedName());

        boolean aadhaarDobMatched =
                Objects.equals(
                        user.getDob(),
                        aadhaarDocument.getExtractedDob());

        // PAN vs AADHAAR
        double documentSimilarity =
                FuzzyMatchingUtil.calculateSimilarity(
                        panDocument.getExtractedName(),
                        aadhaarDocument.getExtractedName());

        boolean documentDobMatched =
                Objects.equals(
                        panDocument.getExtractedDob(),
                        aadhaarDocument.getExtractedDob());

        String finalStatus;
        String remarks;

        if (panSimilarity >= 90
                && aadhaarSimilarity >= 90
                && panDobMatched
                && aadhaarDobMatched
                && documentSimilarity >= 90
                && documentDobMatched) {

            finalStatus = "DOCUMENT_VERIFIED";

            remarks =
                    "PAN and Aadhaar verified successfully";


            kycDetails.setStatus(
                    KycStatus.DOCUMENT_VERIFIED);

            kycDetails.setVerifiedAt(
                    LocalDateTime.now());

        }
        else if (panSimilarity >= 75
                || aadhaarSimilarity >= 75) {

            finalStatus = "UNDER_REVIEW";

            remarks =
                    "Manual verification required";

            kycDetails.setStatus(
                    KycStatus.UNDER_REVIEW);

        }
        else {

            finalStatus = "REJECTED";

            remarks =
                    "Document verification failed";

            kycDetails.setStatus(
                    KycStatus.REJECTED);

            kycDetails.setRejectionReason(
                    remarks);
        }

        kycDetails.setUpdatedAt(
                LocalDateTime.now());

        kycDetailsRepository.save(
                kycDetails);

        return KycVerificationResponseDto
                .builder()
                .userId(userId)
                .panMatchPercentage(
                        panSimilarity)
                .aadhaarMatchPercentage(
                        aadhaarSimilarity)
                .panAadhaarMatchPercentage(
                        documentSimilarity)
                .panDobMatched(
                        panDobMatched)
                .aadhaarDobMatched(
                        aadhaarDobMatched)
                .panAadhaarDobMatched(
                        documentDobMatched)
                .finalStatus(
                        finalStatus)
                .remarks(
                        remarks)
                .build();
    }

    @Transactional
    public VoiceVerificationResponseDto uploadVoice(
            Long userId,
            MultipartFile file) {

        try {

            kycVoiceVerificationRepository
                    .findByUserId(userId)
                    .ifPresent(v -> {
                        throw new KycException(
                                "Voice already uploaded"
                        );
                    });

            KycDetails kycDetails =
                    kycDetailsRepository
                            .findByUserId(userId)
                            .orElseThrow(() ->
                                    new KycException(
                                            "KYC not found"));
            UserResponseDto user =
                    authServiceClient.getUserById(userId);

            if (kycVoiceVerificationRepository
                    .findByUserId(userId)
                    .isPresent()) {

                throw new KycException(
                        "Voice already uploaded"
                );
            }

            if (kycDetails.getStatus()
                    != KycStatus.DOCUMENT_VERIFIED) {

                throw new KycException(
                        "Document verification pending");
            }

            String audioBase64 =
                    AudioUtil.convertToBase64(file);

//            File audioFile =
//                    VoiceFileUtil
//                            .convertBase64ToWavFile(
//                                    audioBase64);
//
//            String speechText =
//                    speechToTextService
//                            .convertSpeechToText(
//                                    audioFile);

            String speechText =
                    sarvamSpeechToTextService
                            .convertSpeechToText(
                                    file);

            ParsedVoiceDataDto parsedData =
                    voiceParserService
                            .parseSpeechText(
                                    speechText);

            KycVoiceVerification voiceVerification =
                    KycVoiceVerification.builder()
                            .userId(userId)
                            .kycId(kycDetails.getId())
                            .audioBase64(audioBase64)
                            .speechText(speechText)

                            // ✅ RAW STORAGE (NO MASKING IN DB)
                            .extractedName(parsedData.getName())
                        //    .extractedDob(parsedData.getDob())
                          .extractedDob(user.getDob()) //adjustment not reading dob from speech to text
                            .extractedPan(parsedData.getPan())
                            .extractedAadhaar(parsedData.getAadhaar())

                            .verificationStatus(
                                    "PENDING")
                            .createdAt(
                                    LocalDateTime.now())
                            .build();

            voiceVerification =
                    kycVoiceVerificationRepository
                            .save(
                                    voiceVerification);

            kycDetails.setStatus(
                    KycStatus.VOICE_PENDING);

            kycDetailsRepository.save(
                    kycDetails);

            return VoiceVerificationResponseDto
                    .builder()
                    .voiceVerificationId(
                            voiceVerification.getId())
                    .userId(userId)
                    .speechText(
                            speechText)
                    .extractedName(
                            parsedData.getName())
                    .extractedDob(
                            user.getDob())  //adjustment
                    .extractedPan(
                            parsedData.getPan())
                    .extractedAadhaar(
                            parsedData.getAadhaar())
                    .status("VOICE_PENDING")
                    .build();

        } catch (Exception ex) {

            throw new KycException(
                    ex.getMessage());
        }
    }


    @Transactional
    public VoiceVerificationResultDto verifyVoice(
            Long userId) {



        KycDetails kycDetails =
                kycDetailsRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new KycException(
                                        "KYC not found"));

        if (kycDetails.getStatus()
                != KycStatus.VOICE_PENDING) {

            throw new KycException(
                    "Voice upload pending"
            );
        }

        KycDocument panDocument =
                kycDocumentRepository
                        .findByKycIdAndDocumentType(
                                kycDetails.getId(),
                                DocumentType.PAN)
                        .orElseThrow(() ->
                                new KycException(
                                        "PAN document not found"));

        KycDocument aadhaarDocument =
                kycDocumentRepository
                        .findByKycIdAndDocumentType(
                                kycDetails.getId(),
                                DocumentType.AADHAAR)
                        .orElseThrow(() ->
                                new KycException(
                                        "AADHAAR document not found"));

        KycVoiceVerification voice =
                kycVoiceVerificationRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new KycException(
                                        "Voice data not found"));

        double nameSimilarity =
                FuzzyMatchingUtil
                        .calculateSimilarity(
                                panDocument.getExtractedName(),
                                voice.getExtractedName());

        boolean dobMatched =
                Objects.equals(
                        panDocument.getExtractedDob(),
                        voice.getExtractedDob());

        boolean panMatched =
                Objects.equals(
                        panDocument.getExtractedDocumentNumber(),
                        voice.getExtractedPan());

        boolean aadhaarMatched =
                Objects.equals(
                        aadhaarDocument.getExtractedDocumentNumber(),
                        voice.getExtractedAadhaar());

        String finalStatus;

        String remarks;

        if (nameSimilarity >= 90
                && dobMatched
                && panMatched
                && aadhaarMatched) {

            finalStatus = "APPROVED";

            remarks =
                    "Voice verification successful";

            voice.setVerificationStatus(
                    "VOICE_VERIFIED");

            kycDetails.setStatus(
                    KycStatus.APPROVED);

            kycDetails.setVerifiedAt(
                    LocalDateTime.now());

        }
        else if (nameSimilarity >= 75) {

            finalStatus =
                    "UNDER_REVIEW";

            remarks =
                    "Manual verification required";

            voice.setVerificationStatus(
                    "UNDER_REVIEW");

            kycDetails.setStatus(
                    KycStatus.UNDER_REVIEW);

        }
        else {

            finalStatus =
                    "REJECTED";

            remarks =
                    "Voice verification failed";

            voice.setVerificationStatus(
                    "REJECTED");

            kycDetails.setStatus(
                    KycStatus.REJECTED);

            kycDetails.setRejectionReason(
                    remarks);
        }

        kycVoiceVerificationRepository
                .save(voice);

        kycDetailsRepository
                .save(kycDetails);

        return VoiceVerificationResultDto
                .builder()
                .userId(userId)
                .nameMatchPercentage(
                        nameSimilarity)
                .dobMatched(
                        dobMatched)
                .panMatched(
                        panMatched)
                .aadhaarMatched(
                        aadhaarMatched)
                .finalStatus(
                        finalStatus)
                .remarks(
                        remarks)
                .build();
    }
}