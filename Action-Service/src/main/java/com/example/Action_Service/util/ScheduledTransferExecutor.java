package com.example.Action_Service.util;

import com.example.Action_Service.dto.TransferTransactionRequest;
import com.example.Action_Service.entity.ScheduledTransfer;
import com.example.Action_Service.enums.ScheduleStatus;
import com.example.Action_Service.enums.ScheduledTransferFrequency;
import com.example.Action_Service.repository.ScheduledTransferRepository;
import com.example.Action_Service.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTransferExecutor {

    private final ScheduledTransferRepository repository;
    private final TransferService transferService;

   // @Scheduled(fixedDelay = 60000)
    public void executeScheduledTransfers() {

        log.info("Checking scheduled transfers...");

        List<ScheduledTransfer> transfers =
                repository.findByStatusAndNextExecutionTimeLessThanEqual(
                        ScheduleStatus.SCHEDULED,
                        LocalDateTime.now()
                );

        if (transfers.isEmpty()) {
            return;
        }

        for (ScheduledTransfer transfer : transfers) {

            try {

                executeTransfer(transfer);

            } catch (Exception ex) {

                log.error(
                        "Scheduled Transfer Failed : {}",
                        transfer.getId(),
                        ex);

                transfer.setStatus(
                        ScheduleStatus.FAILED);

                transfer.setUpdatedAt(
                        LocalDateTime.now());

                repository.save(transfer);
            }
        }
    }

    private void executeTransfer(
            ScheduledTransfer schedule) {

        TransferTransactionRequest request =
                TransferTransactionRequest.builder()
                        .senderUserId(
                                schedule.getSenderUserId())
                        .receiverUserId(
                                schedule.getReceiverUserId())
                        .senderBankId(
                                schedule.getSenderBankId())
                        .receiverBankId(
                                schedule.getReceiverBankId())
                        .amount(
                                schedule.getAmount())
                        .upiPin(
                                schedule.getUpiPin())
                        .transactionType("TRANSFER")
                        .remarks(
                                schedule.getRemarks())
                        .build();

        transferService.transfer(request);

        updateSchedule(schedule);

        log.info(
                "Scheduled Transfer Executed Successfully : {}",
                schedule.getId());
    }

    private void updateSchedule(
            ScheduledTransfer schedule) {

        if (schedule.getFrequency()
                == ScheduledTransferFrequency.ONE_TIME) {

            schedule.setStatus(
                    ScheduleStatus.EXECUTED);
        }

        else if (schedule.getFrequency()
                == ScheduledTransferFrequency.WEEKLY) {

            schedule.setNextExecutionTime(
                    schedule.getNextExecutionTime()
                            .plusWeeks(1));

            schedule.setStatus(
                    ScheduleStatus.SCHEDULED);
        }

        else if (schedule.getFrequency()
                == ScheduledTransferFrequency.MONTHLY) {

            schedule.setNextExecutionTime(
                    schedule.getNextExecutionTime()
                            .plusMonths(1));

            schedule.setStatus(
                    ScheduleStatus.SCHEDULED);
        }

        schedule.setUpdatedAt(
                LocalDateTime.now());

        repository.save(schedule);
    }
}