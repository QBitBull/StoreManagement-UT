package com.sfinance.SFBackend.Controller;

import com.sfinance.SFBackend.Constants.ProductConstants;
import com.sfinance.SFBackend.Entity.Product;
import com.sfinance.SFBackend.Exceptions.ExceptionHandling;
import com.sfinance.SFBackend.Exceptions.ProductExceptions.ProductNameExistException;
import com.sfinance.SFBackend.Exceptions.ProductExceptions.ProductNotFoundException;
import com.sfinance.SFBackend.Security.JWT.CustomHttp.HttpResponse;
import com.sfinance.SFBackend.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/product")
public class ProductController  extends ExceptionHandling {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('product:read')")
    public ResponseEntity<List<Product>> getAllUsers(){
        List<Product> products = productService.getProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/find/{productName}")
    @PreAuthorize("hasAnyAuthority('product:read')")
    public ResponseEntity<Product> getProduct(@PathVariable String productName){
        Product product = productService.findProductByProductName(productName);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('product:create')")
    public ResponseEntity<Product> addNewProduct(@RequestParam("productName")String productName,
                                                 @RequestParam("boughtPrice") String boughtPrice,
                                                 @RequestParam("soldPrice") String soldPrice,
                                                 @RequestParam("nameBrand") String nameBrand,
                                                 @RequestParam("nameCategory") String nameCategory,
                                                 @RequestParam("quantity") String quantity) throws ProductNameExistException, ProductNotFoundException {

        Product newProduct = productService.addNewProduct(productName, Double.parseDouble(boughtPrice), Double.parseDouble(soldPrice),
                nameBrand, nameCategory, Integer.parseInt(quantity));
        return new ResponseEntity<>(newProduct, HttpStatus.OK);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('product:update')")
    public ResponseEntity<Product> updateProduct(@RequestParam("currentProductName")String currentProductName,
                                                 @RequestParam("productName")String productName,
                                                 @RequestParam("boughtPrice") String boughtPrice,
                                                 @RequestParam("soldPrice") String soldPrice,
                                                 @RequestParam("nameBrand") String nameBrand,
                                                 @RequestParam("nameCategory") String nameCategory,
                                                 @RequestParam("quantity") String quantity) throws ProductNameExistException, ProductNotFoundException {

        Product currentProduct = productService.updateProduct(currentProductName,productName, Double.parseDouble(boughtPrice), Double.parseDouble(soldPrice),
                nameBrand, nameCategory, Integer.parseInt(quantity));
        return new ResponseEntity<>(currentProduct, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{productName}")
    @PreAuthorize("hasAnyAuthority('product:delete')")
    public ResponseEntity<HttpResponse> deleteProduct(@PathVariable String productName){
        productService.deleteProduct(productName);
        return response();
    }


    private ResponseEntity<HttpResponse> response() {
        HttpResponse body = new HttpResponse(HttpStatus.NO_CONTENT.value(), HttpStatus.NO_CONTENT, HttpStatus.NO_CONTENT.getReasonPhrase().toUpperCase(), ProductConstants.PRODUCT_DELETED_SUCCESFULLY.toUpperCase());
        return new ResponseEntity<>(body, HttpStatus.NO_CONTENT);
    }
}
