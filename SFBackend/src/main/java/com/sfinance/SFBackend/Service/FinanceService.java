package com.sfinance.SFBackend.Service;

import com.sfinance.SFBackend.Entity.Finance;
import com.sfinance.SFBackend.Exceptions.FinanceException.FinanceDateExistException;
import com.sfinance.SFBackend.Exceptions.FinanceException.FinanceNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface FinanceService {

    List<Finance> getFinances();

    Finance findFinanceByDate(LocalDate date);

    Finance addNewFinance(LocalDate date, Double income) throws FinanceNotFoundException, FinanceDateExistException;

    Finance updateFinance(LocalDate currentDate, LocalDate newDate, Double newIncome) throws FinanceNotFoundException, FinanceDateExistException;

    void deleteFinance(LocalDate date);
}
