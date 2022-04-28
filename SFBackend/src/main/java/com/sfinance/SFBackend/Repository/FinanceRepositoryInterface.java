package com.sfinance.SFBackend.Repository;

import com.sfinance.SFBackend.Entity.Finance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface FinanceRepositoryInterface extends JpaRepository<Finance, Long> {

    Finance findFinanceByDate(LocalDate date);

    void deleteFinanceByDate(LocalDate date);
}
