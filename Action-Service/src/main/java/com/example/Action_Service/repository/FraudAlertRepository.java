package com.example.Action_Service.repository;

import com.example.Action_Service.entity.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudAlertRepository
        extends JpaRepository<FraudAlert,Long> {
}
