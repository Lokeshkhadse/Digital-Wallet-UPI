package com.example.Action_Service.service;

import com.example.Action_Service.dto.ScheduledTransferRequest;
import com.example.Action_Service.dto.ScheduledTransferResponse;
import com.example.Action_Service.entity.ScheduledTransfer;
import com.example.Action_Service.enums.ScheduleStatus;
import com.example.Action_Service.exception.ScheduledTransferNotFoundException;
import com.example.Action_Service.exception.ValidationException;
import com.example.Action_Service.mapper.ScheduledTransferMapper;
import com.example.Action_Service.repository.ScheduledTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduledTransferService {

    private final ScheduledTransferRepository repository;
    private final ScheduledTransferMapper mapper;
    private final UserUpiPinService upiPinService;

    public ScheduledTransferResponse createSchedule(
            ScheduledTransferRequest request) {

        upiPinService.validatePin(
                request.getSenderUserId(),
                request.getSenderBankId(),
                request.getUpiPin()
        );

        if(request.getNextExecutionTime()
                .isBefore(LocalDateTime.now())) {

            throw new ValidationException(
                    "Execution time must be future date/time");
        }

        ScheduledTransfer transfer =
                mapper.toEntity(request);

        ScheduledTransfer saved =
                repository.save(transfer);

        return mapper.toResponse(saved);
    }

    public ScheduledTransferResponse getById(
            Long scheduleId){

        ScheduledTransfer transfer =
                repository.findById(scheduleId)
                        .orElseThrow(() ->
                                new ScheduledTransferNotFoundException(
                                        "Schedule not found with id : "
                                                + scheduleId));

        return mapper.toResponse(transfer);
    }

    public ScheduledTransferResponse cancel(
            Long scheduleId){

        ScheduledTransfer transfer =
                repository.findById(scheduleId)
                        .orElseThrow(() ->
                                new ScheduledTransferNotFoundException(
                                        "Schedule not found with id : "
                                                + scheduleId));

        if(transfer.getStatus()
                == ScheduleStatus.EXECUTED) {

            throw new ValidationException(
                    "Executed schedule cannot be cancelled");
        }

        transfer.setStatus(
                ScheduleStatus.CANCELLED);

        transfer.setUpdatedAt(
                LocalDateTime.now());

        ScheduledTransfer saved =
                repository.save(transfer);

        return mapper.toResponse(saved);
    }
}