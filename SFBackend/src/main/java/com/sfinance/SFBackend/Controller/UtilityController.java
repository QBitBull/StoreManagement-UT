package com.sfinance.SFBackend.Controller;

import com.sfinance.SFBackend.Constants.UtilityConstants;
import com.sfinance.SFBackend.Entity.Utility;
import com.sfinance.SFBackend.Exceptions.UtilityException.UtilityNameExistException;
import com.sfinance.SFBackend.Exceptions.UtilityException.UtilityNotFoundException;
import com.sfinance.SFBackend.Security.JWT.CustomHttp.HttpResponse;
import com.sfinance.SFBackend.Service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/utility")
public class UtilityController {

    private final UtilityService utilityService;

    @Autowired
    public UtilityController(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('utility:read')")
    public ResponseEntity<List<Utility>> getAllUtilities(){
        List<Utility> utilities = utilityService.getUtilities();
        return new ResponseEntity<>(utilities, HttpStatus.OK);
    }

    @GetMapping("/find/{nameUtility}")
    @PreAuthorize("hasAnyAuthority('utility:read')")
    public ResponseEntity<Utility> getUtility(@PathVariable String nameUtility){
        Utility utility = utilityService.findUtilityByNameUtility(nameUtility);
        return new ResponseEntity<>(utility, HttpStatus.OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('utility:create')")
    public ResponseEntity<Utility> addNewUtility(@RequestParam("nameUtility")String nameUtility,
                                                 @RequestParam("priceUtility") String priceUtility) throws UtilityNameExistException, UtilityNotFoundException {

        Utility newUtility = utilityService.addNewUtility(nameUtility, Double.parseDouble(priceUtility));
        return new ResponseEntity<>(newUtility, HttpStatus.OK);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('utility:update')")
    public ResponseEntity<Utility> updateUtility(@RequestParam("currentNameUtility")String currentNameUtility,
                                                 @RequestParam("nameUtility")String nameUtility,
                                                 @RequestParam("priceUtility") String priceUtility) throws UtilityNameExistException, UtilityNotFoundException {
        Utility currentUtility = utilityService.updateUtility(currentNameUtility, nameUtility, Double.parseDouble(priceUtility));
        return new ResponseEntity<>(currentUtility, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{nameUtility}")
    @PreAuthorize("hasAnyAuthority('utility:delete')")
    public ResponseEntity<HttpResponse> deleteUtility(@PathVariable String nameUtility){
        utilityService.deleteUtility(nameUtility);
        return response();
    }


    private ResponseEntity<HttpResponse> response() {
        HttpResponse body = new HttpResponse(HttpStatus.NO_CONTENT.value(), HttpStatus.NO_CONTENT, HttpStatus.NO_CONTENT.getReasonPhrase().toUpperCase(), UtilityConstants.UTILITY_DELETED_SUCCESFULLY.toUpperCase());
        return new ResponseEntity<>(body, HttpStatus.NO_CONTENT);
    }
}
