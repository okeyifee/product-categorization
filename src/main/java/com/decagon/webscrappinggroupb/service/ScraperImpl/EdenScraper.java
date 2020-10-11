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
public class EdenScraper implements Scrapper{

    private final Logger logger = LoggerFactory.getLogger(EdenScraper.class);

    ProductService productService;

    @Autowired
    public EdenScraper(ProductService productService) {
        this.productService = productService;
    }

    List<String> productUrls = new ArrayList<>();
    String baseURL = "https://edenbodyworks.com";

    public void scrape() {
        logger.info("Scrapping Eden");
        getProducts();
    }

    /**
     * Method to get the urls for the individual products
     */
    public void getProductUrls(String url) {
        try {
            Document eden = Jsoup.connect(url).get();
            Elements products = eden.select(".product-link");

            for (Element product : products) {
                productUrls.add("https://edenbodyworks.com" + product.attr("href"));
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
        getProductUrls("https://edenbodyworks.com/collections/all");
        getProductUrls("https://edenbodyworks.com/collections/all?page=2");

        for (String productUrl : productUrls) {
            String name;
            String price;
            String size = "";
            String description = "";
            String ingredients = "";
            String image;
            String brand;
            boolean available = false;
            try {
                Document productPage = Jsoup.connect(productUrl).get();
                String benefits = "";
                String recommendedFor = "";

                /**
                 * Returns the product details:
                 * product name
                 * product price
                 * product brand
                 * product image Url
                 * product ingredient(S)
                 * product description
                 * product availability
                 */

                name = productPage.select(".detail .title").text();
                price = (productPage.select(".price").first().text().replaceAll("[^0-9.]", ""));
                brand = "EDEN" + baseURL.substring(12, 21);
                image = "https:" + productPage.select(".rimage-wrapper noscript img").first().attr("src");

                Elements buttons = productPage.select(".custom-field--title");
                for (Element button : buttons) {
                    switch (button.text()) {
                        case "Ingredients":
                            ingredients = button.nextElementSibling().text();
                            break;
                        case "Benefits":
                            benefits = button.nextElementSibling().text();
                            break;
                        case "Recommended For":
                            recommendedFor = button.nextElementSibling().text();
                            break;
                    }
                }

                if ((benefits + recommendedFor).length() > 0) {
                    description = benefits + recommendedFor;
                }

                if (productPage.getElementsByClass("soldout").text().toLowerCase().equals("sold out")){
                    available = false;
                } else {
                    available = true;
                }

                /**
                 * Validates product to be stored to database
                 */
                if ((ingredients != null) && !(ingredients.isEmpty()) && !(isProductCollections(name))) {
                    double priceNum = Double.parseDouble(price.replaceAll("[$a-zA-Z ]", "")) * 100;
                    Long longPrice = (long) priceNum;
                    Product scrapedProduct = new Product(0, productUrl, name, brand, longPrice, ingredients, description, image, size, available);

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
