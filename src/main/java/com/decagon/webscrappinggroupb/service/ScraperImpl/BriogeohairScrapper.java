package com.decagon.webscrappinggroupb.service.ScraperImpl;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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
public class BriogeohairScrapper implements Scrapper{

    private final Logger logger = LoggerFactory.getLogger(BriogeohairScrapper.class);

    ProductService productService;

    @Autowired
    public BriogeohairScrapper(ProductService productService) {
        this.productService = productService;
    }

    String webUrl = "https://briogeohair.com/collections/all-products";
    String baseURL = "https://briogeohair.com";
    List<Element> productLinks = new CopyOnWriteArrayList<Element>();

    public void scrape() {
        logger.info("Scrapping Briogeohair");
        getProductLinks();
    }

    public void getProductLinks() {
        try {
            Document doc = Jsoup.connect(webUrl).get();
            Elements elements = doc.select("a.newcol-product");
            for (Element element : elements) {
                productLinks.add(element);
            }
            for (Element link : productLinks) {
                getProduct(link);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    public void getProduct(Element link) throws IOException {
        String productLink;
        String ingredients = null;
        Long priceNum;
        String brand;
        String description;
        String image;
        String productName;
        String size = "";
        boolean available = true;

        try {
            // convert page to generated HTML and convert to document
            productLink = baseURL + link.attr("href");
            Document doc = Jsoup.connect(productLink).get();

            String temp2 = doc.select("h1.pdp-details-title").first().text().toLowerCase();
            String str[] = temp2.trim().split("\\s+");
            List<String> al;
            al = Arrays.asList(str);

            /**
             * Returns the product details:
             * product name
             * product price
             * product brand
             * product image url
             * product size
             * product description
             * product ingredient(S)
             * product size
             */

            productName = doc.select("h1.pdp-details-title").first().text();
            String price = doc.select("h5.pdp-details-price > span").attr("flow-default");
            brand = baseURL.substring(8,15);
            image = ("https:" + link.select("img.newcol-product-img-first").attr("src")).split("\\?")[0];
            size = UncleFunkyDaughterScrapper.getString(temp2, size, al);

            String gotten_description = doc.select("div.product-info-content.product-info-rte").get(0).text();
            String hold_string = gotten_description.substring(gotten_description.indexOf("What"));
            description = hold_string.substring(hold_string.indexOf(":")).replace(':',' ').trim();

            if (doc.select("div.product-info-content.product-info-rte").size() > 0) {
                String checkIngredient = doc.select(" span.product-info-tab-inner").text();
                if (checkIngredient.toLowerCase().contains("ingredients")){
                    ingredients = doc.select("div.product-info-content.product-info-rte").get(1).text();
                }
            } else {
                ingredients = " ";
            }

            /**
             * Validates product to be stored to database
             */
            if ((ingredients != null) && !(ingredients.isEmpty()) && !isProductCollections(productName)) {
                priceNum = Long.parseLong(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                Product scrapedProduct = new Product(0, productLink, productName, brand, priceNum, ingredients, description, image, size, available);

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
}

