package com.sfinance.SFBackend.Service;

import com.sfinance.SFBackend.Entity.Product;
import com.sfinance.SFBackend.Entity.Utility;
import com.sfinance.SFBackend.Exceptions.ProductExceptions.ProductNameExistException;

import java.util.List;

public interface PriceCalculatorService {

    double calculatePrice(List<Product> productList,
                          List<Utility> utilities,
                          double profitPercentage,
                          double salesExpectedPercentage) throws ProductNameExistException;
}
