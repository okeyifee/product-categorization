package com.decagon.webscrappinggroupb.service.ScraperImpl;

import com.decagon.webscrappinggroupb.model.Product;
import com.decagon.webscrappinggroupb.service.ProductService;
import com.decagon.webscrappinggroupb.service.ProductDetailService;
import com.decagon.webscrappinggroupb.util.Scrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

    @Component
    public class GirlAndHairScraper implements Scrapper{

        private final Logger logger = LoggerFactory.getLogger(GirlAndHairScraper.class);

        ProductService productService;
        @Autowired
        ProductDetailService productDetailService;

        @Autowired
        public GirlAndHairScraper(ProductService productService) {
            this.productService = productService;
        }

        List<String> productUrls = new ArrayList<>();
        String baseURL = "https://www.girlandhair.com";

        public void scrape() {
            logger.info("Scrapping GirlandHair");
            getProducts();
        }

        /**
         * Method to get the urls for the individual products
         */
        public void getProductUrls() {
            try {
                Document girlAndHair = Jsoup.connect("https://www.girlandhair.com/collections/under-hair-care").get();
                Elements products = girlAndHair.getElementsByClass("product-grid--title");

                for (Element product : products) {
                    productUrls.add(baseURL + product.select("a[href]").attr("href"));
                }
            } catch (IOException ioe) {
                logger.info("Network error " + ioe.getMessage());
                logger.error("Network error " + ioe.getMessage());
            }
        }

        /**
         * Method to get product properties for each product
         */
        public void getProducts() {
            getProductUrls();
            for (String productUrl : productUrls) {
                String name;
                String price;
                String size;
                String description = "";
                String ingredients = "";
                String image;
                String brand;
                String productType = "";
                String suitableHairType = "";
                boolean available;

                try {

                    Document productPage = Jsoup.connect(productUrl).get();

                    /**
                     * Returns the product details:
                     * product name
                     * product price
                     * product brand
                     * product image Url
                     * product size
                     * product availability
                     * product ingredient(S)
                     * product description
                     */

                    name = productPage.select(".product-details-product-title").text();
                    price = (productPage.select(".money").first().text().replaceAll("[^0-9.]", ""));
                    brand = baseURL.substring(12,16) + "+" + baseURL.substring(19,23);
                    image = "https:" + productPage.select(".product-single__photo").first().attr("src");
                    size = productPage.select(".product-description li").last().text();

                    if (productPage.getElementById("AddToCartText").text().toLowerCase().equals("add to cart")){
                        available = true;
                    } else {
                        available = false;
                    }

                    Elements sections = productPage.select("h3");
                    for (Element section : sections) {
                        if (section.text().equals("Ingredients")) {
                            ingredients = section.nextElementSibling().getElementsByTag("em").text();
                        }
                        if (section.text().equals("Description")) {
                            description = section.nextElementSibling().text();
                        }
                    }


//                    String forHairTypeCategorization = description;
//                    System.out.println("forHairTypeCategorization: " + forHairTypeCategorization);


//                    String forProductCategorization = description;
//                    System.out.println("forProductCategorization: " + forProductCategorization);
                    productType = productDetailService.getProductType(description);
                    System.out.println("Description: " + description);

                    suitableHairType = productDetailService.getSuitableHairType(description);
                    System.out.println("Suitable hair type: " + productDetailService.getSuitableHairType(description));




                    /**
                     * Validates product to be stored to database
                     */
                    if ((ingredients != null) && !(ingredients.isEmpty()) && !(isProductCollections(name))) {
                        double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                        Long longPrice = (long) priceNum;
                        Product scrapedProduct = new Product(0, productUrl, name, brand, productType, suitableHairType, longPrice, ingredients, description, image, size, available);

                        /**
                         * Validates if product exist in database
                         *  if Yes: update product details
                         *  if No: save product to database
                         */
                        productService.saveScrappedProduct(name, scrapedProduct);
                    }

                } catch (IOException ioe) {
                    logger.info("Network error " + ioe.getMessage());
                    logger.error("Network error " + ioe.getMessage());
                }
            }
        }
    }
