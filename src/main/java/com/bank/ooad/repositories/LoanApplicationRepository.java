package com.bank.ooad.repositories;

import com.bank.ooad.models.loans.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, String> {
}
