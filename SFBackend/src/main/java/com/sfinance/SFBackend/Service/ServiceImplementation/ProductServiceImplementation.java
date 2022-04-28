package com.sfinance.SFBackend.Service.ServiceImplementation;

import com.sfinance.SFBackend.Constants.ProductConstants;
import com.sfinance.SFBackend.Entity.Product;
import com.sfinance.SFBackend.Exceptions.ProductExceptions.ProductNameExistException;
import com.sfinance.SFBackend.Exceptions.ProductExceptions.ProductNotFoundException;
import com.sfinance.SFBackend.Repository.ProductRepositoryInterface;
import com.sfinance.SFBackend.Service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Qualifier("ProductDetailsService")

public class ProductServiceImplementation implements ProductService {

    private final ProductRepositoryInterface productRepositoryInterface;

    @Autowired
    public ProductServiceImplementation(ProductRepositoryInterface productRepositoryInterface) {
        this.productRepositoryInterface = productRepositoryInterface;
    }

    @Override
    public List<Product> getProducts() {
        return productRepositoryInterface.findAll();
    }

    @Override
    public Product findProductByProductName(String productName) {
        return productRepositoryInterface.findProductByProductName(productName);
    }

    @Override
    public Product addNewProduct(String productName, Double boughtPrice, Double soldPrice, String nameBrand, String nameCategory, Integer quantity) throws ProductNameExistException, ProductNotFoundException {

        validateProductName(StringUtils.EMPTY, productName);
        Product product = new Product();

        product.setProductName(productName);
        product.setBoughtPrice(boughtPrice);
        product.setSoldPrice(soldPrice);
        product.setQuantity(quantity);
        product.setNameBrand(nameBrand);
        product.setNameCategory(nameCategory);

        productRepositoryInterface.save(product);

        return product;
    }

    @Override
    public Product updateProduct(String currentProductName, String newProductName, Double newBoughtPrice, Double newSoldPrice, String newNameBrand, String newNameCategory, Integer newQuantity) throws ProductNameExistException, ProductNotFoundException {

        Product currentProduct = validateProductName(currentProductName, newProductName);

        assert currentProduct != null;
        currentProduct.setProductName(newProductName);
        currentProduct.setBoughtPrice(newBoughtPrice);
        currentProduct.setSoldPrice(newSoldPrice);
        currentProduct.setQuantity(newQuantity);
        currentProduct.setNameBrand(newNameBrand);
        currentProduct.setNameCategory(newNameCategory);

        productRepositoryInterface.save(currentProduct);

        return currentProduct;
    }

    @Override
    public void deleteProduct(String productName) {
        productRepositoryInterface.deleteProductByProductName(productName);
    }

    private Product validateProductName(String currentProductName, String newProductName) throws ProductNotFoundException, ProductNameExistException {

        Product productByNewProductName = findProductByProductName(newProductName);

        if(StringUtils.isNotBlank(currentProductName)){
            Product currentProduct = findProductByProductName(currentProductName);
            if(currentProduct == null){
                throw new ProductNotFoundException(ProductConstants.NO_PRODUCT_FOUND_BY_PRODUCT_NAME + " " + currentProductName);
            }
            if(productByNewProductName != null && !currentProduct.getId().equals(productByNewProductName.getId())){
                throw  new ProductNameExistException(ProductConstants.PRODUCT_NAME_ALREADY_EXISTS);
            }
            return currentProduct;
        }else{
            if(productByNewProductName != null){
                throw  new ProductNameExistException(ProductConstants.PRODUCT_NAME_ALREADY_EXISTS);
            }
            return null;
        }
    }
}
