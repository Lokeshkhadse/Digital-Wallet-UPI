//package com.example.Ledger_Service.service;
//
//import com.example.Ledger_Service.dto.LedgerRequest;
//import com.example.Ledger_Service.dto.LedgerResponse;
//import com.example.Ledger_Service.entity.LedgerTransaction;
//import com.example.Ledger_Service.mapper.LedgerMapper;
//import com.example.Ledger_Service.repository.LedgerRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CachePut;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class LedgerService {
//
//    private final LedgerRepository ledgerRepository;
//    private final LedgerMapper ledgerMapper;
//
////    @CachePut(value = "ledger", key = "#result.ledgerId")
//    public LedgerResponse createEntry(LedgerRequest request) {
//
//        LedgerTransaction ledger = ledgerMapper.toEntity(request);
//        LedgerTransaction saved = ledgerRepository.save(ledger);
//        return ledgerMapper.toResponse(saved);
//    }
//
////    @Cacheable(value = "ledger", key = "#id")
//    public LedgerResponse getLedgerById(Long id) {
//        LedgerTransaction ledger = ledgerRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Ledger not found with id " + id));
//        return ledgerMapper.toResponse(ledger);
//    }
//
////    @Cacheable(value = "ledger_all")
//    public List<LedgerResponse> getAllLedgers() {
//        return ledgerRepository.findAll().stream()
//                .map(ledgerMapper::toResponse)
//                .toList();
//    }
//}



package com.example.Ledger_Service.service;

import com.example.Ledger_Service.dto.LedgerRequest;
import com.example.Ledger_Service.dto.LedgerResponse;
import com.example.Ledger_Service.entity.LedgerTransaction;
import com.example.Ledger_Service.mapper.LedgerMapper;
import com.example.Ledger_Service.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final LedgerMapper ledgerMapper;


    //    @CachePut(value = "ledger", key = "#result.ledgerId")
    public LedgerResponse createEntry(LedgerRequest request) {

        log.info(
                "Creating ledger entry | TransactionRef={} | UserId={} | Amount={}",
                request.getTransactionRefNo(),
                request.getUserId(),
                request.getAmount());

        LedgerTransaction ledger =
                ledgerMapper.toEntity(request);

        LedgerTransaction saved =
                ledgerRepository.save(ledger);

        log.info(
                "Ledger entry created successfully | LedgerId={} | TransactionRef={}",
                saved.getLedgerId(),
                saved.getTransactionRefNo());

        return ledgerMapper.toResponse(saved);
    }


    //    @Cacheable(value = "ledger", key = "#id")
    public LedgerResponse getLedgerById(Long id) {

        log.info(
                "Fetching ledger by id | LedgerId={}",
                id);

        LedgerTransaction ledger =
                ledgerRepository.findById(id)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Ledger not found | LedgerId={}",
                                    id);

                            return new RuntimeException(
                                    "Ledger not found with id " + id);
                        });

        log.info(
                "Ledger fetched successfully | LedgerId={}",
                id);

        return ledgerMapper.toResponse(ledger);
    }


    //    @Cacheable(value = "ledger_all")
    public List<LedgerResponse> getAllLedgers() {

        log.info("Fetching all ledger entries");

        List<LedgerResponse> ledgers =
                ledgerRepository.findAll()
                        .stream()
                        .map(ledgerMapper::toResponse)
                        .toList();

        log.info(
                "Fetched {} ledger entries",
                ledgers.size());

        return ledgers;
    }
}