package com.sfinance.SFBackend.Repository;

import com.sfinance.SFBackend.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepositoryInterface extends JpaRepository<Product, Long> {

    Product findProductByProductName(String productName);

    void deleteProductByProductName(String productName);
}
