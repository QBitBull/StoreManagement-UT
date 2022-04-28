package com.sfinance.SFBackend.Service.ServiceImplementation;

import com.sfinance.SFBackend.Entity.Product;
import com.sfinance.SFBackend.Entity.Utility;
import com.sfinance.SFBackend.Exceptions.ProductExceptions.ProductNameExistException;
import com.sfinance.SFBackend.Service.PriceCalculatorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Qualifier("Price Calculator")
public class PriceCalculatorServiceImplementation implements PriceCalculatorService {
    @Override
    public double calculatePrice(List<Product> productList, List<Utility> utilities, double profitPercentage, double salesExpectedPercentage) throws ProductNameExistException {

        double utilitiesCost = utilities.stream()
                .map(Utility::getPriceUtility)
                .reduce(0.0, Double::sum);

        double productsCost = productList.stream()
                .map(l -> l.getBoughtPrice() * l.getQuantity())
                .reduce(0.0, Double::sum);

        double totalCost = utilitiesCost + productsCost;

        double income = ((100 + profitPercentage) * totalCost) / 100;  // objective

        double productsIncome = productList.stream()
                .map(l -> l.getBoughtPrice() * ((salesExpectedPercentage / 100) * l.getQuantity()))
                .reduce(0.0, Double::sum);

        double increasePercentage = 100;
        double desiredIncome = 0;

        while (desiredIncome < income) {
            increasePercentage += 10;
            desiredIncome = (increasePercentage / 100) * productsIncome;
        }
        return increasePercentage;
    }

}