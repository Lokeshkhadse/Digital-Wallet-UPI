package com.example.Action_Service.mapper;

import com.example.Action_Service.dto.ScheduledTransferRequest;
import com.example.Action_Service.dto.ScheduledTransferResponse;
import com.example.Action_Service.entity.ScheduledTransfer;
import com.example.Action_Service.enums.ScheduleStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScheduledTransferMapper {

    public ScheduledTransfer toEntity(
            ScheduledTransferRequest request) {

        return ScheduledTransfer.builder()
                .senderUserId(request.getSenderUserId())
                .receiverUserId(request.getReceiverUserId())
                .senderBankId(request.getSenderBankId())
                .receiverBankId(request.getReceiverBankId())
                .amount(request.getAmount())
                .frequency(request.getFrequency())
                .status(ScheduleStatus.SCHEDULED)
                .nextExecutionTime(request.getNextExecutionTime())
                .upiPin(request.getUpiPin())
                .remarks(request.getRemarks())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public ScheduledTransferResponse toResponse(
            ScheduledTransfer transfer) {

        return ScheduledTransferResponse.builder()
                .id(transfer.getId())
                .senderUserId(transfer.getSenderUserId())
                .receiverUserId(transfer.getReceiverUserId())
                .amount(transfer.getAmount())
                .frequency(transfer.getFrequency())
                .status(transfer.getStatus())
                .nextExecutionTime(
                        transfer.getNextExecutionTime())
                .build();
    }
}