package com.sfinance.SFBackend.Service;

import com.sfinance.SFBackend.Entity.Product;
import com.sfinance.SFBackend.Entity.Utility;
import com.sfinance.SFBackend.Exceptions.ProductExceptions.ProductNameExistException;
import com.sfinance.SFBackend.Exceptions.ProductExceptions.ProductNotFoundException;
import com.sfinance.SFBackend.Exceptions.UtilityException.UtilityNameExistException;
import com.sfinance.SFBackend.Exceptions.UtilityException.UtilityNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface UtilityService {

    List<Utility> getUtilities();

    Utility findUtilityByNameUtility(String nameUtility);

    Utility addNewUtility(String nameUtility, Double priceUtility) throws UtilityNameExistException, UtilityNotFoundException;

    Utility updateUtility(String currentNameUtility, String newNameUtility, Double newPriceUtility) throws UtilityNameExistException, UtilityNotFoundException;

    void deleteUtility(String nameUtility);

}
