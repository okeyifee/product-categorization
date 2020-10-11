package com.decagon.webscrappinggroupb.service;

import com.decagon.webscrappinggroupb.model.Product;

public interface ProductService{

    void saveProduct(Product product);
    Product getProductByName(String name);
    void saveScrappedProduct(String productName, Product scrappedProduct);
}
