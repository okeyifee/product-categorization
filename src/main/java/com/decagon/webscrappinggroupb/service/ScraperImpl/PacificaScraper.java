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
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//public class PacificaScraper implements Scrapper{
//
//    private final Logger logger = LoggerFactory.getLogger(PacificaScraper.class);
//
//    ProductService productService;
//
//    @Autowired
//    public PacificaScraper(ProductService productService) {
//        this.productService = productService;
//    }
//
//    List<String> productUrls = new ArrayList<>();
//    String baseUrl = "https://www.pacificabeauty.com";
//
//    public void scrape() {
//        logger.info("Scrapping pacifica");
//        getProducts();
//    }
//
//    /**
//     * Method to get the urls for the individual products
//     */
//    public void getProductUrls() {
//        try {
//            Document pacifica = Jsoup.connect("https://www.pacificabeauty.com/collections/hair").get();
//            System.out.println(pacifica.title());
//            Elements products = pacifica.select(".ProductItem__Title");
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
//
//            String name;
//            String price;
//            String size;
//            String description;
//            String ingredients = null;
//            String image;
//            String brand;
//            boolean available = false;
//
//            try {
//                Document productPage = Jsoup.connect(productUrl).get();
//
//                /**
//                 * Returns the product details:
//                 * product name
//                 * product brand
//                 * product size
//                 * product image
//                 * product price
//                 * product description
//                 */
//                name = productPage.select(".ProductMeta__Title").text();
//                brand = baseUrl.substring(12,20);
//                size = "";
//                image = "https:" + productPage.select(".Product__Slideshow img").attr("data-original-src");
//                price = (productPage.select(".ProductMeta__Price").first().text().replaceAll("[^0-9.]", ""));
//                description = productPage.select(".ProductMeta__Description .Rte > p").first().text();
//
//                /**
//                 * Returns the product Availability
//                 */
//                String extract = productPage.select(".Text--subdued").text();
//                String convr =  "";
//
//                if (extract.toLowerCase().contains("pieces")){
//                    String str[] = extract.trim().split("\\s+");
//                    List<String> al;
//                    al = Arrays.asList(str);
//
//                    for (String check : al) {
//                        if (check.equals("pieces")){
//                            String pivot = al.get(al.indexOf("pieces") - 1);
//                            convr += pivot;
////                            logger.info("available products in stock: " + convr);
//                            break;
//                        }
//                    }
//                    int availableProducts = Integer.parseInt(convr);
//                    if (availableProducts > 0){
//                        available = true;
//                    } else {
//                        available = false;
//                    }
//                }
//
//                /**
//                 * Returns the product ingredients
//                 */
//                Elements siblingButtons = productPage.select(".Collapsible__Button");
//                for (Element button : siblingButtons) {
//                    if (button.text().equals("Ingredients")) {
//                        ingredients = button.nextElementSibling().text();
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
//            } catch (IOException ioe) {
//                logger.info("Network error " + ioe.getMessage());
//                logger.error("Network error " + ioe.getMessage());
//            }
//        }
//    }
//}