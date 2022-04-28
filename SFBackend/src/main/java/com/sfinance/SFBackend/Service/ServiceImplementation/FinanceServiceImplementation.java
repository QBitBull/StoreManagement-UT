package com.sfinance.SFBackend.Service.ServiceImplementation;

import com.sfinance.SFBackend.Constants.FinanceConstants;
import com.sfinance.SFBackend.Entity.Finance;
import com.sfinance.SFBackend.Exceptions.FinanceException.FinanceDateExistException;
import com.sfinance.SFBackend.Exceptions.FinanceException.FinanceNotFoundException;
import com.sfinance.SFBackend.Repository.FinanceRepositoryInterface;
import com.sfinance.SFBackend.Service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@Qualifier("FinanceDetailsService")

public class FinanceServiceImplementation implements FinanceService {

    private final FinanceRepositoryInterface financeRepositoryInterface;

    @Autowired
    public FinanceServiceImplementation(FinanceRepositoryInterface financeRepositoryInterface) {
        this.financeRepositoryInterface = financeRepositoryInterface;
    }

    @Override
    public List<Finance> getFinances() {
        return financeRepositoryInterface.findAll();
    }

    @Override
    public Finance findFinanceByDate(LocalDate date) {
        return financeRepositoryInterface.findFinanceByDate(date);
    }

    @Override
    public Finance addNewFinance(LocalDate date, Double income) throws FinanceDateExistException {

        validateNewUser(date);

        Finance finance = new Finance();

        finance.setDate(date);
        finance.setIncome(income);

        financeRepositoryInterface.save(finance);

        return finance;
    }

    @Override
    public Finance updateFinance(LocalDate currentDate, LocalDate newDate, Double newIncome) throws FinanceNotFoundException, FinanceDateExistException {

        Finance currentFinance = validateUpdate(currentDate, newDate);

        currentFinance.setDate(newDate);
        currentFinance.setIncome(newIncome);

        financeRepositoryInterface.save(currentFinance);

        return currentFinance;
    }

    @Override
    public void deleteFinance(LocalDate date) {
        financeRepositoryInterface.deleteFinanceByDate(date);
    }

    private Finance validateUpdate(LocalDate currentDate, LocalDate newDate) throws FinanceNotFoundException, FinanceDateExistException {

        Finance currentFinance = findFinanceByDate(currentDate);
        if(currentFinance == null){
            throw new FinanceNotFoundException(FinanceConstants.NO_FINANCE_FOUND_BY_FINANCE_DATE);
        }else{
            Finance newFinance = findFinanceByDate(newDate);
            if(newFinance != null && !currentFinance.getId().equals(newFinance.getId())){
                throw new FinanceDateExistException(FinanceConstants.FINANCE_DATE_ALREADY_EXISTS);
            }
        }
        return currentFinance;
    }

    private void validateNewUser(LocalDate newDate) throws FinanceDateExistException {
        Finance finance = findFinanceByDate(newDate);
        if(finance != null){
            throw new FinanceDateExistException(FinanceConstants.FINANCE_DATE_ALREADY_EXISTS);
        }
    }
}
