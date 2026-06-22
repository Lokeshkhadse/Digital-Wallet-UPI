package com.example.Action_Service.mapper;

import com.example.Action_Service.dto.CreateSplitBillRequest;
import com.example.Action_Service.dto.SplitBillResponse;
import com.example.Action_Service.entity.SplitBill;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SplitBillMapper {

    public SplitBill toEntity(
            CreateSplitBillRequest request) {

        return SplitBill.builder()
                .createdByUserId(
                        request.getCreatedByUserId())
                .receiverBankId(
                        request.getReceiverBankId())
                .title(
                        request.getTitle())
                .description(
                        request.getDescription())
                .totalAmount(
                        request.getTotalAmount())
                .splitType(
                        request.getSplitType())
                .dueDate(
                        request.getDueDate())
                .expiryDate(
                        request.getExpiryDate())
                .build();
    }

    public SplitBillResponse toResponse(
            SplitBill bill) {

        return SplitBillResponse.builder()
                .splitBillId(
                        bill.getSplitBillId())
                .createdByUserId(
                        bill.getCreatedByUserId())
                .title(
                        bill.getTitle())
                .description(
                        bill.getDescription())
                .totalAmount(
                        bill.getTotalAmount())
                .paidAmount(
                        bill.getPaidAmount())
                .remainingAmount(
                        bill.getRemainingAmount())
                .splitType(
                        bill.getSplitType())
                .status(
                        bill.getStatus())
                .dueDate(
                        bill.getDueDate())
                .expiryDate(
                        bill.getExpiryDate())
                .createdAt(
                        bill.getCreatedAt())
                .build();
    }
}