package com.sfinance.SFBackend.Service.ServiceImplementation;

import com.sfinance.SFBackend.Constants.UtilityConstants;
import com.sfinance.SFBackend.Entity.Utility;
import com.sfinance.SFBackend.Exceptions.UtilityException.UtilityNameExistException;
import com.sfinance.SFBackend.Exceptions.UtilityException.UtilityNotFoundException;
import com.sfinance.SFBackend.Repository.UtilityRepositoryInterface;
import com.sfinance.SFBackend.Service.UtilityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Qualifier("UtilityDetailsService")

public class UtilityServiceImplementation implements UtilityService {

    private final UtilityRepositoryInterface utilityRepositoryInterface;

    @Autowired
    public UtilityServiceImplementation(UtilityRepositoryInterface utilityRepositoryInterface) {
        this.utilityRepositoryInterface = utilityRepositoryInterface;
    }

    @Override
    public List<Utility> getUtilities() {
        return utilityRepositoryInterface.findAll();
    }

    @Override
    public Utility findUtilityByNameUtility(String nameUtility) {
        return utilityRepositoryInterface.findUtilityByNameUtility(nameUtility);
    }

    @Override
    public Utility addNewUtility(String nameUtility, Double priceUtility) throws UtilityNameExistException, UtilityNotFoundException {
        validateUtilityName(StringUtils.EMPTY, nameUtility);
        Utility utility = new Utility();

        utility.setNameUtility(nameUtility);
        utility.setPriceUtility(priceUtility);

        utilityRepositoryInterface.save(utility);

        return utility;
    }

    @Override
    public Utility updateUtility(String currentNameUtility, String newNameUtility, Double newPriceUtility) throws UtilityNameExistException, UtilityNotFoundException {

        Utility currentUtility = validateUtilityName(currentNameUtility, newNameUtility);

        assert currentUtility != null;
        currentUtility.setNameUtility(newNameUtility);
        currentUtility.setPriceUtility(newPriceUtility);

        utilityRepositoryInterface.save(currentUtility);

        return currentUtility;
    }

    @Override
    public void deleteUtility(String nameUtility) {
        utilityRepositoryInterface.deleteUtilityByNameUtility(nameUtility);
    }

    private Utility validateUtilityName(String currentUtilityName, String newUtilityName) throws UtilityNameExistException, UtilityNotFoundException {

        Utility utilityByNewUtilityName = findUtilityByNameUtility(newUtilityName);

        if(StringUtils.isNotBlank(currentUtilityName)){
            Utility currentUtility = findUtilityByNameUtility(currentUtilityName);
            if(currentUtility == null){
                throw new UtilityNotFoundException(UtilityConstants.NO_UTILITY_FOUND_BY_UTILITY_NAME + " " + currentUtilityName);
            }
            if(utilityByNewUtilityName != null && !currentUtility.getId().equals(utilityByNewUtilityName.getId())){
                throw  new UtilityNameExistException(UtilityConstants.UTILITY_NAME_ALREADY_EXISTS);
            }
            return currentUtility;
        }else{
            if(utilityByNewUtilityName != null){
                throw  new UtilityNameExistException(UtilityConstants.UTILITY_NAME_ALREADY_EXISTS);
            }
            return null;
        }
    }

}
