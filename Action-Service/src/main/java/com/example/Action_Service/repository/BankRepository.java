package com.example.Action_Service.repository;

import com.example.Action_Service.entity.UserBankDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<UserBankDetails,Long> {
    boolean existsByAccountNumber(@NotBlank(message = "Account number is required") String accountNumber);

    boolean existsByUpiId(@Pattern(
            regexp = "^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$",
            message = "Invalid UPI ID format (e.g. name@upi)"
    ) String upiId);
}
