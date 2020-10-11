//package com.decagon.webscrappinggroupb.service.ScraperImpl;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.CopyOnWriteArrayList;
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
//@Component
//public class UncleFunkyDaughterScrapper implements Scrapper{
//
//    private final Logger logger = LoggerFactory.getLogger(UncleFunkyDaughterScrapper.class);
//
//    ProductService productService;
//
//    @Autowired
//    public UncleFunkyDaughterScrapper(ProductService productService) {
//        this.productService = productService;
//    }
//
//    final String webUrl = "https://unclefunkysdaughter.com/hair-care.html";
//    String baseURL = "https://unclefunkysdaughter.com";
//    List<Element> productLinks = new CopyOnWriteArrayList<>();
//
//    public void scrape() {
//        logger.info("Scrapping UncleFunky'sDaughter");
//        getProductLinks();
//    }
//
//    public void getProductLinks() {
//        try {
//            Document doc = Jsoup.connect(webUrl).get();
//            Elements links = doc.select(".product.photo.product-item-photo > a:first-child");
//            for (Element link : links) {
//                getProduct(link);
//                productLinks.add(link);
//            }
//        } catch (IOException ioe) {
//            logger.info("Network error " + ioe.getMessage());
//            logger.error("Network error " + ioe.getMessage());
//        }
//    }
//
//    public void getProduct(Element link) throws IOException {
//        String productLink;
//        String ingredients;
//        String price;
//        String description;
//        String image;
//        String productName;
//        String size = "";
//        String brand;
//        boolean available;
//
//        try {
//            // convert page to generated HTML and convert to document
//            productLink = link.attr("href");
//            Document doc = Jsoup.connect(productLink).get();
//
//            /**
//             * Returns the product details:
//             * product name
//             * product brand
//             * product image
//             * product price
//             * product description
//             * product ingredients
//             */
//            productName = doc.select("h1.page-title > span").text();
//            brand = baseURL.substring(8,27);
//            image = link.child(0).attr("data-src");
//            price = doc.select("div.product-info-price > div.price-box.price-final_price > span.price-container.price-final_price.tax.weee > span.price-wrapper > span.price").text();
//            description = doc.select("div.product-description").text();
//            ingredients = doc.select("div.ingredient > div > table tbody > tr > td").text();
//
//            /**
//             * Returns the product Availability
//             */
//            if (doc.select("div.product-info-stock-sku > div.stock.available > span.label").next().text().toLowerCase().equals("in stock")){
//                available = true;
//            } else {
//                available = false;
//            }
//
//            /**
//             * Returns the product size
//             */
//            String temp2 = doc.select("h1.page-title > span").text().toLowerCase();
//            String str[] = temp2.trim().split("\\s+");
//            String str2[] = description.trim().split("\\s+");
//            List<String> al;
//            List<String> al2;
//            al = Arrays.asList(str);
//            al2 = Arrays.asList(str2);
//
//            if (temp2.contains("oz")) {
//                size = getString(size, al);
//            } else {
//                size = getString(description, size, al2);
//            }
//
//            /**
//             * validates product to be sored in database
//             */
//            if (ingredients != null && !ingredients.isEmpty() && !isProductCollections(productName)) {
//                double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
//                Long longPrice = (long) priceNum;
//                Product scrapedProduct = new Product(0, productLink, productName, brand, longPrice, ingredients, description, image, size, available);
//
//                /**
//                 * Validates if product exist in database
//                 *  if Yes: update product details
//                 *  if No: save product to database
//                 */
//                productService.saveScrappedProduct(productName, scrapedProduct);
//            }
//
//        } catch (IOException ioe) {
//            logger.info("Network error " + ioe.getMessage());
//            logger.error("Network error " + ioe.getMessage());
//        }
//    }
//
//    /**
//     * returns product size if it's contained in the product description
//     */
//    static String getString(String description, String size, List<String> al2) {
//        if (description.contains("oz")) {
//            size = getString(size, al2);
//        }
//        return size;
//    }
//
//    /**
//     * returns product size if it's contained in the product name
//     */
//    private static String getString(String size, List<String> al2) {
//        for (String name : al2) {
//            if (name.contains("oz") && name.length() == 2) {
//                int hold = al2.indexOf(name);
//                String pivot = al2.get(hold - 1);
//                size += pivot;
//                size += al2.get(hold);
//                break;
//            }
//            if (name.contains("oz") && name.length() > 2) {
//                size += name;
//                break;
//            }
//        }
//        return size;
//    }
//}
//
