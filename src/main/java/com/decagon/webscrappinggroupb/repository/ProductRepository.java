package com.decagon.webscrappinggroupb.repository;

import com.decagon.webscrappinggroupb.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Double>{

    Product findByName(String name);
}
