package com.example.Action_Service.repository;

import com.example.Action_Service.entity.UserBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<UserBankDetails,Long> {
}
