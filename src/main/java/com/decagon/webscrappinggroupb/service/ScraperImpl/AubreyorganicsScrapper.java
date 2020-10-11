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
//@Component
//public class AubreyorganicsScrapper implements Scrapper{
//
//     private final Logger logger = LoggerFactory.getLogger(AubreyorganicsScrapper.class);
//
//    ProductService productService;
//
//    @Autowired
//    public AubreyorganicsScrapper(ProductService productService) {
//        this.productService = productService;
//    }
//
//    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4999.61 Safari/537.36";
//    private final String baseUrl = "https://aubreyorganics.com/collections";
//
//    public void scrape() {
//        logger.info("Scrapping Aubreyorganics");
//        shampooProperties();
//        conditionerProperties();
//        stylingProperties();
//    }
//
//    /**
//     * return product brand
//     */
//    String brand = baseUrl.substring(8,14);
//
//    /**
//     * Scrap for shampooos
//     */
//    public void shampooProperties() {
//        List<String> productCollectionLinks = new ArrayList<>();
//        try {
//            String ingredients;
//            boolean available;
//            String sampooUrl = baseUrl + "/shampoo";
//            Document document = Jsoup.connect(sampooUrl).userAgent(USER_AGENT).get();
//            Elements elements = document.getElementsByClass("collection__product__container");
//
//            for (Element element : elements) {
//                String link = element.getElementsByClass("collection__product__link").attr("href");
//                productCollectionLinks.add(sampooUrl + link);
//            }
//
//            for (String link : productCollectionLinks) {
//                Document doc = Jsoup.connect(link).get();
//
//                /**
//                 * Returns the product details:
//                 * product name
//                 * product image Url
//                 * product price
//                 * product size
//                 * product description
//                 * product link
//                 * product ingredient(S)
//                 * product availability
//                 */
//                String productName = doc.getElementsByClass("pdp-title").text();
//                String image = "https:" + doc.select(" div.swiper-slide >img").attr("src");
//                String price = doc.getElementById("nutra__fullPrice").text();
//                String size = doc.getElementsByClass("pdp-subtitle").text();
//                String description = doc.getElementsByClass("product__description").select(">div >p").text();
//                String productLink = link;
//                ingredients = doc.getElementsByClass("section_content").text();
//
//                if (doc.select("div.pdp_out_of_stock > p").text().toLowerCase().equals("this item is currently out of stock")){
//                    available = false;
//                } else {
//                    available = true;
//                }
//
//                /**
//                 * Validates product to be stored to database
//                 */
//                if ((ingredients != null) && !(ingredients.isEmpty()) && !isProductCollections(productName)) {
//                    double productPrice = (Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100);
//                    Long newPrice = (long) productPrice;
//                    Product scrapedProduct = new Product(0, productLink, productName, brand, newPrice, ingredients, description, image, size, available);
//
//                    /**
//                     * Validates if product exist in database
//                     *  if Yes: update product details
//                     *  if No: save product to database
//                     */
//                    productService.saveScrappedProduct(productName, scrapedProduct);
//                }
//            }
//        } catch (IOException ioe) {
//            logger.info("Network error " + ioe.getMessage());
//            logger.error("Network error " + ioe.getMessage());
//        }
//    }
//
//    /**
//     * Scrap for conditioners
//     */
//    public void conditionerProperties() {
//        List<String> productCollectionLinks = new ArrayList<>();
//        try {
//            boolean available;
//            String sampooUrl = baseUrl + "/conditioners";
//            Document document = Jsoup.connect(sampooUrl).userAgent(USER_AGENT).get();
//            Elements elements = document.getElementsByClass("collection__product__container");
//
//            for (Element element : elements) {
//                String link = element.getElementsByClass("collection__product__link").attr("href");
//                productCollectionLinks.add(sampooUrl + link);
//            }
//
//            for (String link : productCollectionLinks) {
//                Document doc = Jsoup.connect(link).get();
//
//                /**
//                 * Returns the product details:
//                    * product name
//                    * product image Url
//                    * product price
//                    * product size
//                    * product description
//                    * product link
//                    * product ingredient(S)
//                    * product availability
//                 */
//                String productName = doc.getElementsByClass("pdp-title").text();
//                String image = "https:" + doc.select(" div.swiper-slide >img").attr("src");
//                String price = doc.getElementById("nutra__fullPrice").text();
//                String size = doc.getElementsByClass("pdp-subtitle").text();
//                String description = doc.getElementsByClass("product__description").select(">div >p").text();
//                String productLink = link;
//                String ingredients = doc.getElementsByClass("section_content").text();
//
//                if (doc.select("div.pdp_out_of_stock > p").text().toLowerCase().equals("this item is currently out of stock")){
//                    available = false;
//                } else {
//                    available = true;
//                }
//
//                /**
//                 * Validates product to be stored to database
//                 */
//                if ((ingredients != null) && !(ingredients.isEmpty()) && !isProductCollections(productName)) {
//                    double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
//                    Long longPrice = (long) priceNum;
//                    Product scrapedProduct = new Product(0, productLink, productName, brand, longPrice, ingredients, description, image, size, available);
//
//                    /**
//                     * Validates if product exist in database
//                     *  if Yes: update product details
//                     *  if No: save product to database
//                     */
//                    productService.saveScrappedProduct(productName, scrapedProduct);
//                }
//            }
//        } catch (IOException ioe) {
//            logger.info("Network error " + ioe.getMessage());
//            logger.error("Network error " + ioe.getMessage());
//        }
//    }
//
//    /**
//     * Scrap for styling products
//     */
//    public void stylingProperties() {
//        List<String> productCollectionLinks = new ArrayList<>();
//        try {
//            boolean available;
//            String sampooUrl = baseUrl + "/styling";
//            Document document = Jsoup.connect(sampooUrl).userAgent(USER_AGENT).get();
//            Elements elements = document.getElementsByClass("collection__product__container");
//
//            for (Element element : elements) {
//                String link = element.getElementsByClass("collection__product__link").attr("href");
//                productCollectionLinks.add(sampooUrl + link);
//            }
//
//            for (String link : productCollectionLinks) {
//                Document doc = Jsoup.connect(link).get();
//
//                /**
//                 * Returns the product details:
//                    * product name
//                    * product price
//                    * product image
//                    * product size
//                    * product ingredients
//                    * product link
//                    * product description
//                    * product size
//                 */
//                String productName = doc.getElementsByClass("pdp-title").text();
//                String price = doc.getElementById("nutra__fullPrice").text();
//                String image = "https:" + doc.select(" div.swiper-slide >img").attr("src");
//                String size = doc.getElementsByClass("pdp-subtitle").text();
//                String ingredients = doc.getElementsByClass("section_content").text();
//                String productLink = link;
//                String description = doc.getElementsByClass("product__description").select(">div >p").text();
//
//                if (doc.select("div.pdp_out_of_stock > p").text().toLowerCase().equals("this item is currently out of stock")){
//                    available = false;
//                } else {
//                    available = true;
//                }
//
//                /**
//                 * Validates product to be stored to database
//                 */
//                if ((ingredients != null) && !(ingredients.isEmpty()) && !(isProductCollections(productName))) {
//                    double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
//                    Long longPrice = (long) priceNum;
//                    Product scrapedProduct = new Product(0, productLink, productName, brand, longPrice, ingredients, description, image, size, available);
//
//                    /**
//                     * Validates if product exist in database
//                     *  if Yes: update product details
//                     *  if No: save product to database
//                     */
//                    productService.saveScrappedProduct(productName, scrapedProduct);
//                }
//            }
//        } catch (IOException ioe) {
//            logger.info("Network error " + ioe.getMessage());
//            logger.error("Network error " + ioe.getMessage());
//        }
//    }
//}