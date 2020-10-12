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
public class HoneysScrapper implements Scrapper{

    private final Logger logger = LoggerFactory.getLogger(HoneysScrapper.class);

    ProductService productService;

    @Autowired
    public HoneysScrapper(ProductService productService) {
        this.productService = productService;
    }

    private final List<String> honeyProductUrls = new ArrayList<>();
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4999.61 Safari/537.36";
    private static final String baseUrl = "https://www.honeyshandmade.com/collections/hair-care?page=";


    public void scrape() {
        logger.info("Scrapping Honeys");
        productUrl();
    }

    public void productPageUrl() {
        for (int i = 1; i <= 8; i++) {
            String pageUrl = baseUrl + i;
            honeyProductUrls.add(pageUrl);
        }
    }

    public void productUrl() {
        productPageUrl();
        for (String url : honeyProductUrls) {
            String productLink;
            String ingredients;
            String price;
            String description = "";
            String image;
            String productName;
            String size = "";
            String brand;
            String productType = "";
            String suitableHairType = "";
            boolean available = false;

            try {
                final Document productsPage = Jsoup.connect(url).userAgent(USER_AGENT).get();
                Elements elements = productsPage.getElementsByClass("aspect-product__wrapper");

                for (Element element : elements) {

                    /**
                     * Returns the product link
                     */
                    productLink = "https://www.honeyshandmade.com/" + element.attr("href");

                    final Document productPage = Jsoup.connect(productLink).get();

                    /**
                     * Returns the product details:
                     * product name
                     * product price
                     * product brand
                     * product image Url
                     * product ingredient(S)
                     * product description
                     * product availability
                     * product size
                     */
                    productName = productPage.getElementsByClass("product_title entry-title").text();
                    price = productPage.getElementById("ProductPrice-product-template").text();
                    brand = "Honey's " + baseUrl.substring(18, 26);
                    image = productPage.getElementsByClass("zoom_enabled zoom FeaturedImage-product-template").attr("href");

                    if (productPage.select(".large-6 .product_infos .product-inner-data > div:eq(3)") != null) {
                        description = productPage.select(".large-6 .product_infos .product-inner-data > div:eq(3)").text();
                    }


                    String text1 = productPage.select(".product-tabs .panel ").text().toLowerCase();
                    int lengthOfText1 = text1.length() - 1;
                    int stopIndexOfIngredient;
                    if (text1.contains("ingredients:")) {
                        int startIndexOfIngredient = text1.indexOf("ingredients:");
                        if (text1.contains("instructions:") && (text1.indexOf("instructions:") > text1.indexOf("ingredients:"))) {
                            stopIndexOfIngredient = text1.indexOf("instructions:");
                        } else {
                            stopIndexOfIngredient = lengthOfText1;
                        }
                        System.out.println("\n");
                        System.out.println(productName);
                        ingredients = text1.substring(startIndexOfIngredient, stopIndexOfIngredient);
                    } else if (text1.contains("stuffing")) {
                        int startIndexOfIngredient = text1.indexOf("stuffing");
                        if (text1.contains("how to use")) {
                            stopIndexOfIngredient = text1.indexOf("how to use:");
                        } else {
                            stopIndexOfIngredient = lengthOfText1;
                        }
                        System.out.println("\n");
                        System.out.println(productName);
                        ingredients = text1.substring(startIndexOfIngredient, stopIndexOfIngredient);
                    } else if (text1.contains("quinoa - rich")) {
                        int startIndexOfIngredient = text1.indexOf("quinoa - rich");
                        System.out.println("\n");
                        System.out.println(productName);
                        ingredients = text1.substring(startIndexOfIngredient, lengthOfText1);
                    } else {
                        System.out.println("\n");
                        System.out.println(productName);
                        ingredients = "";
                    }

                    System.out.println(ingredients);

                    if (ingredients.contains("Ingredients:") || ingredients.contains("Stuffing") || ingredients.contains("INGREDIENTS:")) {
                        if (productPage.getElementsByClass("panel").text().toLowerCase().equals("add to cart")) {
                            available = true;
                        } else {
                            available = false;
                        }
                    }

                    if (productPage.select(".large-6 .product_infos .product-inner-data > div:eq(3)") != null) {
                        String productInfo = productPage.select(".product-tabs .panel ").text();
                        productType = productInfo + description;
                        suitableHairType = productInfo + description;
                    } else {
                        productType = description;
                        suitableHairType = description;
                    }


                    /**
                     * Validates product to be stored to database
                     */
                    if ((ingredients != null) && !ingredients.isEmpty() && !isProductCollections(productName)) {
                        double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                        Long longPrice = (long) priceNum;
                        Product scrapedProduct = new Product(0, productLink, productName, brand, productType, suitableHairType, longPrice, ingredients, description, image, size, available);

                        /**
                         * Validates if product exist in database
                         *  if Yes: update product details
                         *  if No: save product to database
                         */
                        productService.saveScrappedProduct(productName, scrapedProduct);
                    }
                }
            } catch (IOException ioe) {
                logger.info("Network error " + ioe.getMessage());
                logger.error("Network error " + ioe.getMessage());
            }
        }
    }
}
