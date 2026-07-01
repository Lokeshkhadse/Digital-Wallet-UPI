package com.example.Ledger_Service.repository;

import com.example.Ledger_Service.entity.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepository
        extends JpaRepository<LedgerTransaction, Long> {
}
