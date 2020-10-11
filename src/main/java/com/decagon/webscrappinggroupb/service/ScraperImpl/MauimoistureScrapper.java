package com.decagon.webscrappinggroupb.service.ScraperImpl;

import com.decagon.webscrappinggroupb.model.Product;
import com.decagon.webscrappinggroupb.service.ProductService;
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
public class MauimoistureScrapper implements Scrapper{

    private final Logger logger = LoggerFactory.getLogger(AlikayNaturalsScrapper.class);

    ProductService productService;

    @Autowired
    public MauimoistureScrapper(ProductService productService) {
        this.productService = productService;
    }

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4999.61 Safari/537.36";
    String baseUrl = "https://www.mauimoisture.com/shop/?swoof=1&pa_product-type=conditioner";

    /**
     * return product brand
     */
    String brand = baseUrl.substring(12, 16) + " " + baseUrl.substring(16,24);

    public void scrape() {
        logger.info("Scrapping mauimoisture");
        mauipageUrlConditioner();
        mauipageUrlShampoo();
        mauipageUrlStyle();
        mauipageUrlTreatProtect();
    }

    /**
     * Scrap for shampoo
     */
    public void mauipageUrlShampoo() {

        try {
            final List<String> mauiPoductLink = new ArrayList<>();
            Document document = Jsoup.connect("https://www.mauimoisture.com/shop/?swoof=1&pa_product-type=shampoo").userAgent(USER_AGENT).get();
            Elements elements = document.select("div.woocommerce");

            for (Element element : elements.select("li")) {
                String urls = element.getElementsByClass("woocommerce-LoopProduct-link").attr("href");
                mauiPoductLink.add(urls);
            }

            for (String link : mauiPoductLink) {
                Document document1 = Jsoup.connect(link).get();
                Elements elements1 = document1.getElementsByClass("row");

                for (Element elem : elements1) {

                    String price = "";
                    String brand = "";
                    boolean available;
                    String size = "";

                    /**
                     * Returns the product details:
                     * product link
                     * product name
                     * product description
                     * product ingredients
                     * product image Url
                     * product availability
                     * product ingredient(S)
                     * product size
                     */
                    String productLink = link;
                    String productName = elem.getElementsByClass("product-item__category").text();
                    String description = elem.getElementById("collapseOne").text();
                    String ingredients = elem.getElementById("collapseThree").text();
                    String image = elem.getElementsByClass("zoom first").attr("href");

                    if (elem.getElementsByClass("buy-online-retailer-block").text().toLowerCase().equals("in stock")) {
                        available = true;
                    } else {
                        available = false;
                    }
//                    String pricing = elem.select("div.buy > span").attr("href");

                    /**
                     * Returns the product size
                     */
                    if (image.contains("oz.jpg")) {
                        size = image.substring(image.length() - 8, image.length() - 4);
                    }

                    /**
                     * Validates product to be stored in database
                     */
                    if ((ingredients != null) && !ingredients.isEmpty() && !isProductCollections(productName)) {
                        Long longPrice;
                        if (price == null || price.isEmpty() || price.isBlank()){
                            longPrice = 0L;
                        } else {
                            double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                            longPrice = (long) priceNum;
                        }
                        Product scrapedProduct = new Product(0, productLink, productName, brand, longPrice, ingredients, description, image, size, available);


                        /**
                         * Validates if product exist in database
                         *  if Yes: update product details
                         *  if No: save product to database
                         */
                        productService.saveScrappedProduct(productName, scrapedProduct);
                    }
                }
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    /**
     * Scrap for conditioners
     */
    public void mauipageUrlConditioner() {
        try {
            final List<String> mauiPoductLink = new ArrayList<>();
            Document document = Jsoup.connect(baseUrl).userAgent(USER_AGENT).get();
            Elements elements = document.select("div.woocommerce");

            for (Element element : elements.select("li")) {
                String urls = element.getElementsByClass("woocommerce-LoopProduct-link").attr("href");
                mauiPoductLink.add(urls);
            }

            for (String link : mauiPoductLink) {
                Document document1 = Jsoup.connect(link).get();
                Elements elements1 = document1.getElementsByClass("row");

                for (Element elem : elements1) {

                    String price = null;
                    boolean available = false;
                    String size = null;

                    /**
                     * Returns the product details:
                     * product link
                     * product name
                     * product description
                     * product ingredients
                     * product image Url
                     */
                    String productLink = link;
                    String productName = elem.getElementsByClass("product-item__category").text();
                    String description = elem.getElementById("collapseOne").text();
                    String ingredients = elem.getElementById("collapseThree").text();
                    String image = elem.getElementsByClass("zoom first").attr("href");

                    /**
                     * Returns the product size
                     */
                    if (image.contains("oz.jpg")) {
                       size = image.substring(image.length() - 8, image.length() - 4);
                    }

                    /**
                     * Validates product to be stored in database
                     */
                    if (ingredients != null && !ingredients.isEmpty()  && !isProductCollections(productName)) {
                        Long longPrice;
                        if (price == null || price.isEmpty() || price.isBlank()){
                            longPrice = 0L;
                        } else {
                            double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                            longPrice = (long) priceNum;
                        }
                        Product scrapedProduct = new Product(0, productLink, productName, brand, longPrice, ingredients, description, image, size, available);


                        /**
                         * Validates if product exist in database
                         *  if Yes: update product details
                         *  if No: save product to database
                         */
                        productService.saveScrappedProduct(productName, scrapedProduct);
                    }
                }
            }

        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }


    /**
     * Scrap for styling products
     */
    public void mauipageUrlStyle() {
        try {
            final List<String> mauiPoductLink = new ArrayList<>();
            Document document = Jsoup.connect("https://www.mauimoisture.com/shop/type/styling/?swoof=1&pa_product-type=styling&really_curr_tax=114-product_tag").userAgent(USER_AGENT).get();
            Elements elements = document.select("div.woocommerce");

            for (Element element : elements.select("li")) {
                String urls = element.getElementsByClass("woocommerce-LoopProduct-link").attr("href");
                mauiPoductLink.add(urls);
            }

            for (String link : mauiPoductLink) {
                Document document1 = Jsoup.connect(link).get();
                Elements elements1 = document1.getElementsByClass("row");

                for (Element elem : elements1) {
                    String price = null;
                    boolean available = false;
                    String size = null;

                    /**
                     * Returns the product details:
                     * product link
                     * product name
                     * product description
                     * product ingredients
                     * product image Url
                     */
                    String productLink = link;
                    String productName = elem.getElementsByClass("product-item__category").text();
                    String description = elem.getElementById("collapseOne").text();
                    String ingredients = elem.getElementById("collapseThree").text();
                    String image = elem.getElementsByClass("zoom first").attr("href");

                    /**
                     * Returns the product size
                     */
                    if (image.contains("oz.jpg")) {
                        size = image.substring(image.length() - 8, image.length() - 4);
                    }

                    /**
                     * Validates product to be stored in database
                     */
                    if (ingredients != null && !ingredients.isEmpty()  && price != null && !isProductCollections(productName)) {
                        Long longPrice;
                        if (price == null || price.isEmpty() || price.isBlank()){
                            longPrice = 0L;
                        } else {
                            double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                            longPrice = (long) priceNum;
                        }
                        Product scrapedProduct = new Product(0, productLink, productName,brand, longPrice, ingredients, description, image, size, available);


                        /**
                         * Validates if product exist in database
                         *  if Yes: update product details
                         *  if No: save product to database
                         */
                        productService.saveScrappedProduct(productName, scrapedProduct);
                    }
                }
            }

        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }


    /**
     * Scrap for Treat and protect products
     */
    public void mauipageUrlTreatProtect() {
        try {
            final List<String> mauiPoductLink = new ArrayList<>();
            Document document = Jsoup.connect("https://www.mauimoisture.com/shop/type/treatments/?swoof=1&pa_product-type=treatments&really_curr_tax=118-product_tag").userAgent(USER_AGENT).get();
            Elements elements = document.select("div.woocommerce");

            for (Element element : elements.select("li")) {
                String urls = element.getElementsByClass("woocommerce-LoopProduct-link").attr("href");
                mauiPoductLink.add(urls);
            }

            for (String link : mauiPoductLink) {
                Document document1 = Jsoup.connect(link).get();
                Elements elements1 = document1.getElementsByClass("row");

                for (Element elem : elements1) {
                    String price = "";
                    boolean available = false;
                    String size = null;
                    String productLink = link;

                    /**
                     * Returns the product details:
                     * product link
                     * product name
                     * product description
                     * product ingredients
                     * product image Url
                     */
                    String productName = elem.getElementsByClass("product-item__category").text();
                    String description = elem.getElementById("collapseOne").text();
                    String ingredients = elem.getElementById("collapseThree").text();
                    String image = elem.getElementsByClass("zoom first").attr("href");

                    /**
                     * Returns the product size
                     */
                    if (image.contains("oz.jpg")) {
                        size = image.substring(image.length() - 8, image.length() - 4);
                    }

                    /**
                     * Validates product to be stored in database
                     */
                    if (ingredients != null && !ingredients.isEmpty() && !isProductCollections(productName)) {
                        Long longPrice;
                        if (price == null || price.isEmpty() || price.isBlank()){
                            longPrice = 0L;
                        } else {
                            double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                            longPrice = (long) priceNum;
                        }
                        Product scrapedProduct = new Product(0, productLink, productName, brand, longPrice, ingredients, description, image, size, available);

                            /**
                             * Validates if product exist in database
                             *  if Yes: update product details
                             *  if No: save product to database
                             */
                        productService.saveScrappedProduct(productName, scrapedProduct);
                        }
                    }
                }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }
}