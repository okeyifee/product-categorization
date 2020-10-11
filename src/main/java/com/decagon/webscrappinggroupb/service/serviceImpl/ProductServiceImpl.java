package com.decagon.webscrappinggroupb.service.serviceImpl;

import com.decagon.webscrappinggroupb.model.Product;
import com.decagon.webscrappinggroupb.repository.ProductRepository;
import com.decagon.webscrappinggroupb.service.ProductService;

import com.decagon.webscrappinggroupb.service.ScraperImpl.CurlSmithMainScrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService{


    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public Product getProductByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public void saveScrappedProduct(String productName, Product scrappedProduct) {

        try{

            Product product = productRepository.findByName(productName);
            Long newLongPrice = scrappedProduct.getPrice();
            if (product == null){
                if (newLongPrice == null || newLongPrice == 0L) {
                    return;
                }
                productRepository.save(scrappedProduct);
                logger.info("successfully saved product: " + productName);
            } else if (product != null) {
                scrappedProduct.setId(product.getId());
                if (newLongPrice == null || newLongPrice == 0L) {
                    scrappedProduct.setAvailable(false);
                }
                productRepository.save(scrappedProduct);
                logger.info("successfully updated product: " + productName + " id: " + product.getId());
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }
}
