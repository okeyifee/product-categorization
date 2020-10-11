//package com.decagon.webscrappinggroupb.service.ScraperImpl;
//
//import com.decagon.webscrappinggroupb.model.Product;
//import com.decagon.webscrappinggroupb.service.ProductService;
//import com.decagon.webscrappinggroupb.util.Scrapper;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Component
//public class TheDouxScraper implements Scrapper{
//
//    private final Logger logger = LoggerFactory.getLogger(TheDouxScraper.class);
//
//    ProductService productService;
//
//    @Autowired
//    public TheDouxScraper(ProductService productService) {
//        this.productService = productService;
//    }
//
//    List<String> productUrls = new ArrayList<>();
//    String baseUrl = "https://thedoux.com";
//
//    public void scrape() {
//        logger.info("Scrapping Doux");
//        getProducts();
//    }
//
//    /**
//     * Method to get the urls for the individual products
//     */
//    public void getProductUrls() {
//        try {
//            Document theDoux = Jsoup.connect("https://thedoux.com/products").get();
//            Elements products = theDoux.select(".product");
//
//            for (Element product : products) {
//                productUrls.add(baseUrl + product.select("a").attr("href"));
//            }
//        } catch (IOException ioe) {
//            logger.info("Network error " + ioe.getMessage());
//            logger.error("Network error " + ioe.getMessage());
//        }
//    }
//
//    /**
//     * Method to get product properties for each product
//     */
//    public void getProducts() {
//        getProductUrls();
//        for (String productUrl : productUrls) {
//            String name;
//            String price;
//            String size;
//            String description = null;
//            String ingredients = null;
//            String image;
//            String brand;
//            boolean available;
//            try {
//                Document productPage = Jsoup.connect(productUrl).get();
//                Elements desc = productPage.select(".product-description strong");
//
//                if (!desc.text().contains("Ingredients:")) {
//                    continue;
//                }
//
//                /**
//                 * Returns the product details:
//                 * product name
//                 * product image
//                 * product brand
//                 * product size
//                 */
//                name = productPage.select(".page-title").text();
//                image = productPage.select("#productSlideshow img").attr("data-src");
//                brand = baseUrl.substring(8,15);
//                size = "";
//
//                /**
//                 * return product availability
//                 */
//                if (productPage.getElementById("productDetails").getElementsByClass("product-mark").text().toLowerCase().equals("sold out")){
//                    available = false;
//                } else {
//                    available = true;
//                }
//
//                /**
//                 * Returns the product price
//                 */
//                price = (productPage.select("#productDetails .sqs-money-native").first().text().replaceAll("[^0-9.]", ""));
//
//                /**
//                 * Returns the product ingredients and description
//                 */
//                for (Element element : desc) {
//                    if (element.text().equals("Ingredients:")) {
//                        ingredients = element.parent().text().substring(element.parent().text().indexOf("Ingredients: "))
//                                .replaceAll("Ingredients: ", "");
//                    }
//                    if (element.text().contains("WHAT IT DOUX")) {
//                        description = element.parent().text().replaceAll("WHAT IT DOUX[:?] ", "");
//                        if (element.parent().text().equals(element.text()))
//                            description = element.parent().nextElementSibling().text();
//                    }
//                }
//
//                /**
//                 * validates product to be sored in database
//                 */
//                if (ingredients != null && !ingredients.isEmpty() && !isProductCollections(name)) {
//                    double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
//                    Long longPrice = (long) priceNum;
//                    Product scrapedProduct = new Product(0, productUrl, name, brand, longPrice, ingredients, description, image, size, available);
//
//                    /**
//                     * Validates if product exist in database
//                     *  if Yes: update product details
//                     *  if No: save product to database
//                     */
//                    productService.saveScrappedProduct(name, scrapedProduct);
//                }
//
//            } catch (IOException ioe) {
//                logger.info("Network error " + ioe.getMessage());
//                logger.error("Network error " + ioe.getMessage());
//            }
//        }
//    }
//}
