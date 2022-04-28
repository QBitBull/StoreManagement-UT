package com.sfinance.SFBackend.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String productName;
    private Double boughtPrice;
    private Double soldPrice;
    private String nameBrand;
    private String nameCategory;
    private Integer quantity;
}

