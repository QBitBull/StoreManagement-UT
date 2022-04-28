package com.sfinance.SFBackend.Service;

import com.sfinance.SFBackend.Entity.Product;
import com.sfinance.SFBackend.Exceptions.ProductExceptions.ProductNameExistException;
import com.sfinance.SFBackend.Exceptions.ProductExceptions.ProductNotFoundException;

import java.util.List;

public interface ProductService {

    List<Product> getProducts();

    Product findProductByProductName(String productName);

    Product addNewProduct(String productName, Double boughtPrice, Double soldPrice, String nameBrand, String nameCategory, Integer quantity) throws ProductNameExistException, ProductNotFoundException;

    Product updateProduct(String currentProductName, String newProductName, Double newBoughtPrice, Double newSoldPrice, String newNameBrand, String newNameCategory, Integer newQuantity) throws ProductNameExistException, ProductNotFoundException;

    void deleteProduct(String productName);

}
