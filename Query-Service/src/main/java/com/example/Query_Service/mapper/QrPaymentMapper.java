//package com.example.Query_Service.mapper;
//
//import com.example.Query_Service.dto.GenerateQrResponse;
//import com.example.Query_Service.entity.QrPayment;
//import org.springframework.stereotype.Component;
//
//@Component
//public class QrPaymentMapper {
//
//    public GenerateQrResponse toResponse(
//            QrPayment qr) {
//
//        return GenerateQrResponse.builder()
//                .qrId(qr.getQrId())
//                .userId(qr.getUserId())
//                .userBankId(qr.getUserBankId())
//                .upiId(qr.getUpiId())
//                .qrContent(qr.getQrContent())
//                .qrImageUrl(qr.getQrImageUrl())
//                .build();
//    }
//}