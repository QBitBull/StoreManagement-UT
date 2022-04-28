package com.sfinance.SFBackend.Controller;

import com.sfinance.SFBackend.Entity.Product;
import com.sfinance.SFBackend.Entity.Utility;
import com.sfinance.SFBackend.Exceptions.ProductExceptions.ProductNameExistException;
import com.sfinance.SFBackend.Exceptions.ProductExceptions.ProductNotFoundException;
import com.sfinance.SFBackend.Security.JWT.CustomHttp.HttpResponse;
import com.sfinance.SFBackend.Service.PriceCalculatorService;
import com.sfinance.SFBackend.Service.ProductService;
import com.sfinance.SFBackend.Service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/priceCalculator")
public class PriceCalculatorController {

    ProductService productService;
    PriceCalculatorService priceCalculatorService;
    UtilityService utilityService;

    @Autowired
    public PriceCalculatorController(ProductService productService, PriceCalculatorService priceCalculatorService, UtilityService utilityService) {
        this.productService = productService;
        this.priceCalculatorService = priceCalculatorService;
        this.utilityService = utilityService;
    }

    private ResponseEntity<HttpResponse> response(HttpStatus status, String message) {
        HttpResponse body = new HttpResponse(status.value(), status, status.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body,status);
    }

    @GetMapping ("/calculate/{profit}/{sales}")
    @PreAuthorize("hasAnyAuthority('price:read')")
    public ResponseEntity<HttpResponse> calculatePrice(@PathVariable("profit") Double profit,
                                                       @PathVariable("sales") Double salesPercentage) throws ProductNameExistException {
        List<Product> productList = productService.getProducts();
        List<Utility> utilitiesList = utilityService.getUtilities();
        double percentage = priceCalculatorService.calculatePrice(productList, utilitiesList, profit, salesPercentage);
        return response(HttpStatus.OK, "The estimated price percentage increased is "+ percentage);
    }

    @GetMapping("/update/{profit}")
    @PreAuthorize("hasAnyAuthority('price:update')")
    public ResponseEntity<?> updatePrices(@PathVariable("profit") Double profit) throws ProductNameExistException, ProductNotFoundException {
        List<Product> productList = productService.getProducts();
        for (Product product : productList) {
            product.setSoldPrice((profit / 100) * product.getBoughtPrice());
            productService.updateProduct(product.getProductName(),
                    product.getProductName(),
                    product.getBoughtPrice(),
                    product.getSoldPrice(),
                    product.getNameBrand(),
                    product.getNameCategory(),
                    product.getQuantity());
        }
        return response(HttpStatus.OK, "Updated Successfully".toUpperCase());
    }
}
