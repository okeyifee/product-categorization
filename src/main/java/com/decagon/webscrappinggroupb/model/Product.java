package com.decagon.webscrappinggroupb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

    @Data
    @Entity
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "products")
    public class Product{

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(length = 500)
        private String productURL;

        private String name;

        private String brandName;

        private String productType;

        private String suitableHairType;

        private Long price;

        @Column(length = 8000)
        private String ingredients;

        @Column(length = 3000)
        private String description;

        @Column(length = 1000)
        private String productImage;

        private String size;
        private boolean available;

        @Override
        public String toString() {
            return "name= '" + getName() + "'\n" +
                    "productURL= '" + getProductURL() + "'\n" +
                    "price= '" + getPrice() + "'\n" +
                    "Brand= '" + getBrandName() + "'\n" +
                    "ingredients= '" + getIngredients() + "'\n" +
                    "description= '" + getDescription() + "'\n" +
                    "productImage= '" + getProductImage() + "'\n" +
                    "availability= '" + isAvailable()+ "'\n" +
                    "suitableHairType= '" + getSuitableHairType()+ "'\n" +
                    "productType= '" + getProductType()+ "'\n" +
                    "size='" + getSize() + "'\n\n";
        }

    }


