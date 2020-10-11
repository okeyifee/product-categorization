package com.decagon.webscrappinggroupb.service.ScraperImpl;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

@Component
public class MelaninHairCareScrapper implements Scrapper{

    private final Logger logger = LoggerFactory.getLogger(AlikayNaturalsScrapper.class);

    ProductService productService;

    @Autowired
    public MelaninHairCareScrapper(ProductService productService) {
        this.productService = productService;
    }

    String webUrl = "https://melaninhaircare.com/collections/frontpage";
    String baseURL = "https://melaninhaircare.com";
    List<Element> productLinks = new CopyOnWriteArrayList<>();
    Pattern pattern = Pattern.compile(".*(shampoo|cream|oil|conditioner).*", Pattern.CASE_INSENSITIVE);

    public void scrape() {
        logger.info("Scrapping MelaninHairCare");
        getProductLinks();
    }

    public void getProductLinks() {
        try {
            Document doc = Jsoup.connect(webUrl).get();
            Elements elements = doc.select("div.product");

            for (Element element : elements) {
                if (element.select("div.ci > div.so.icn").isEmpty() && filterNonHairProduct(element)) {
                    productLinks.add(element.select("div.ci > a").first());
                }
            }
            for (Element link : productLinks) {
                getProduct(link);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        } catch (Exception exception) {
                logger.info("Network error " + exception.getMessage());
                logger.error("Network error " + exception.getMessage());
        }
    }

    private boolean filterNonHairProduct(Element element) {
        String productName = element.select(".product-details a > h3").text();
        Matcher matcher = pattern.matcher(productName);
        return matcher.matches();
    }

    public void getProduct(Element link) throws Exception {
        String productLink;
        String ingredients;
        String price;
        String description;
        String image;
        String productName;
        String size = "";
        String brand;
        boolean available;

        try {
            // convert page to generated HTML and convert to document
            productLink = baseURL + link.attr("href");
            Document doc = Jsoup.connect(productLink).get();

            /**
             * Returns the product details:
             * product name
             * product price
             * product description
             * product image url
             * product ingredients
             * product brand
             */
            productName = doc.getElementById("product-description").child(0).text();
            price = doc.getElementById("product-price").child(0).text();
            description = doc.select("div.rte").first().child(2).text();
            image = ("https:" + doc.getElementById("product-main-image").select("img").attr("src")).split("\\?")[0];
            ingredients = getIngredient(doc);
            brand = baseURL.substring(8,15);

            /**
             * return product size
             */
            String temp2 = doc.getElementsByClass("qualityIngredients").text().toLowerCase();
            String[] str = temp2.trim().split("\\s+");
            List<String> al;
            al = Arrays.asList(str);
            
            if (temp2.contains("oz")) {
                size = getString(size, al);
            } else {
                String sample = doc.getElementsByClass("rte").get(0).text().toLowerCase();
                String[] str2 = sample.trim().split("\\s+");
                List<String> al2;
                al2 = Arrays.asList(str2);
                size = getString(size, al2);
            }

            /**
             * return availability
             */
            if (doc.select("span.product-price").text().toLowerCase().equals("sold out")){
                available = false;
            } else {
                available = true;
            }

            /**
             * Validates product to be stored in database
             */
            if (ingredients != null && !ingredients.isEmpty() && !isProductCollections(productName)) {
                double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                Long longPrice = (long) priceNum;
                Product scrapedProduct = new Product(0, productLink, productName, brand, longPrice, ingredients, description, image, size, available);
                logger.info("Got to this point");

                /**
                 * Validates if product exist in database
                 *  if Yes: update product details
                 *  if No: save product to database
                 */
                productService.saveScrappedProduct(productName, scrapedProduct);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    /**
     * Method to return size
     */
    private String getString(String size, List<String> al2) {
        size = GreenCollectionScraper.getString(size, al2);
        return size;
    }

    /**
     * Method to return product ingredient
     */
    public String getIngredient(Document doc) throws IOException {
        return doc.select("table > tbody > tr > td").text();
    }
}
