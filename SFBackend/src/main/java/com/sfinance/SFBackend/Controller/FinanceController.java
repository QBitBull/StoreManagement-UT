package com.sfinance.SFBackend.Controller;

import com.sfinance.SFBackend.Constants.FinanceConstants;
import com.sfinance.SFBackend.Entity.Finance;
import com.sfinance.SFBackend.Exceptions.FinanceException.FinanceDateExistException;
import com.sfinance.SFBackend.Exceptions.FinanceException.FinanceNotFoundException;
import com.sfinance.SFBackend.Security.JWT.CustomHttp.HttpResponse;
import com.sfinance.SFBackend.Service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/finance")
public class FinanceController {

    private final FinanceService financeService;

    @Autowired
    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('finance:read')")
    public ResponseEntity<List<Finance>> getAllFinances(){
        List<Finance> finances = financeService.getFinances();
        return new ResponseEntity<>(finances, HttpStatus.OK);
    }

    @GetMapping("/find/{date}")
    @PreAuthorize("hasAnyAuthority('finance:read')")
    public ResponseEntity<Finance> getFinance(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        Finance finance = financeService.findFinanceByDate(date);
        return new ResponseEntity<>(finance, HttpStatus.OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('finance:create')")
    public ResponseEntity<Finance> addNewFinance(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                 @RequestParam("income") String income) throws FinanceNotFoundException, FinanceDateExistException {

        Finance newFinance = financeService.addNewFinance(date, Double.parseDouble(income));
        return new ResponseEntity<>(newFinance, HttpStatus.OK);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('finance:update')")
    public ResponseEntity<Finance> updateFinance(@RequestParam("currentDate") @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate currentDate,
                                                 @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                 @RequestParam("income") String income) throws FinanceNotFoundException, FinanceDateExistException {
        Finance currentFinance = financeService.updateFinance(currentDate, date, Double.parseDouble(income));
        return new ResponseEntity<>(currentFinance, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{nameDate}")
    @PreAuthorize("hasAnyAuthority('finance:delete')")
    public ResponseEntity<HttpResponse> deleteFinance(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate nameDate){
        financeService.deleteFinance(nameDate);
        return response();
    }


    private ResponseEntity<HttpResponse> response() {
        HttpResponse body = new HttpResponse(HttpStatus.NO_CONTENT.value(), HttpStatus.NO_CONTENT, HttpStatus.NO_CONTENT.getReasonPhrase().toUpperCase(), FinanceConstants.FINANCE_DELETED_SUCCESSFULLY.toUpperCase());
        return new ResponseEntity<>(body, HttpStatus.NO_CONTENT);
    }
}
