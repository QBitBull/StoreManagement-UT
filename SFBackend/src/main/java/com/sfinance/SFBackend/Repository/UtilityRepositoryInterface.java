package com.sfinance.SFBackend.Repository;

import com.sfinance.SFBackend.Entity.Utility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilityRepositoryInterface extends JpaRepository<Utility, Long> {

    Utility findUtilityByNameUtility(String nameUtility);

    void deleteUtilityByNameUtility(String nameUtility);
}
