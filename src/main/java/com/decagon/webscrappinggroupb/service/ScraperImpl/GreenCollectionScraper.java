package com.decagon.webscrappinggroupb.service.ScraperImpl;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import com.decagon.webscrappinggroupb.model.Product;
import com.decagon.webscrappinggroupb.service.ProductDetailService;
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
public class GreenCollectionScraper implements Scrapper{

    private final Logger logger = LoggerFactory.getLogger(GreenCollectionScraper.class);

    ProductService productService;
    ProductDetailService productDetailService;

    @Autowired
    public GreenCollectionScraper(ProductService productService,
                                  ProductDetailService productDetailService) {
        this.productService = productService;
        this.productDetailService = productDetailService;
    }

    final String webUrl = "https://curls.biz/";
    List<Element> productLinks = new CopyOnWriteArrayList<>();
    List<String> collections = new CopyOnWriteArrayList<>();

    public void scrape() {
        logger.info("Scrapping CurlS");
        getProductLinks();
    }

    public void getProductLinks() {
        try {
            Document doc = Jsoup.connect(webUrl).get();

            Elements links = doc.select("#menu-collections > li.menu-item.menu-item-type-post_type > a");
            for (Element link : links) {
                collections.add(link.attr("href"));
            }

            for (String link : collections) {
                doc = Jsoup.connect(link).get();
                links = doc.select("h3.product-title > a");
                for (Element element : links) {
                    productLinks.add(element);
                    getProduct(element);
                }
            }

        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    public void getProduct(Element link) throws IOException {
        String productLink;
        String ingredients;
        String price;
        String description;
        String image;
        String productName;
        String size = "";
        String brand;
        String productType = "";
        String suitableHairType = "";
        boolean available;

        try {
            // convert page to generated HTML and convert to document
            Document doc = Jsoup.connect(link.attr("href")).get();

            /**
             * Returns the product details:
             * product name
             * product link
             * product price
             * product brand
             * product image Url
             * product ingredient(S)
             * product description
             * product availability
             * product size
             */

            productName = doc.select("h1.product_title.entry-title").first().text();
            productLink = link.attr("href");
            price = doc.select("span.woocommerce-Price-amount.amount > bdi").first().text();
            brand = webUrl.substring(8, 13);
            image = doc.select("figure.woocommerce-product-gallery__wrapper > div > a > img").attr("src");
            ingredients = doc.select("div#tab-ingredient_tab").text();
            description = doc.select("div.post-content.woocommerce-product-details__short-description > p:first-child").text();

            if (doc.select("div.summary-container > div.avada-availability > p.stock.in-stock ").text().toLowerCase().equals("in stock")) {
                available = true;
            } else {
                available = false;
            }

            String temp2 = doc.select("h1.product_title.entry-title").first().text().toLowerCase();
            String str[] = temp2.trim().split("\\s+");
            List<String> al;
            al = Arrays.asList(str);

            if (temp2.contains("oz")) {
                size = getString(size, al);
            } else {
                size = doc.select("div.post-content.woocommerce-product-details__short-description > p > strong").text();
            }



            String forProductDetails = doc.getElementsByClass("posted_in").text() + " " + doc.getElementsByClass("tagged_as").text();
            productType = productDetailService.getProductType(forProductDetails);

            String forHairDetails = doc.select("div.post-content.woocommerce-product-details__short-description > p > a > .alignnone ").attr("alt");
            suitableHairType = productDetailService.getSuitableHairType(forHairDetails);


            /**
             * Validates product to be stored to database
             */
            if ((ingredients != null) && !ingredients.isEmpty() && !isProductCollections(productName)) {
                double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                Long longPrice = (long) priceNum;
                Product scrapedProduct = new Product(0, productLink, productName, brand,  productType, suitableHairType, longPrice, ingredients, description, image, size, available);

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

    static String getString(String size, List<String> al) {
        for (String name : al) {
            if (name.contains("oz") && name.length() == 2) {
                int hold = al.indexOf(name);
                String pivot = al.get(hold - 1);
                size += pivot;
                size += al.get(hold);
            } else if (name.contains("oz") && name.length() > 2){
                size += name;
            }
        }
        return size;
    }
}


