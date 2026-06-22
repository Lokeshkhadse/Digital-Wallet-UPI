package com.example.Query_Service.mapper;


import com.example.Query_Service.dto.SplitBillParticipantResponse;
import com.example.Query_Service.dto.SplitBillResponse;
import com.example.Query_Service.entity.SplitBill;
import com.example.Query_Service.entity.SplitBillParticipant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SplitBillMapper {


    public SplitBillResponse toResponse(
            SplitBill bill, List<SplitBillParticipantResponse> participants) {

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
                .participants(
                        participants)
                .build();
    }
}