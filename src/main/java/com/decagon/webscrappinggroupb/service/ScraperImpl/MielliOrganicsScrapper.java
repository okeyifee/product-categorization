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
public class MielliOrganicsScrapper implements Scrapper{

    private final Logger logger = LoggerFactory.getLogger(MielliOrganicsScrapper.class);

    ProductService productService;

    @Autowired
    public MielliOrganicsScrapper(ProductService productService) {
        this.productService = productService;
    }

    String webUrl = "https://mielleorganics.com/collections/all";
    String baseURL = "https://mielleorganics.com";
    List<Element> productLinks = new CopyOnWriteArrayList<Element>();

    public void scrape() {
        logger.info("Scrapping MielliOrganics");
        getProductLinks();
    }

    public void getProductLinks() {
        try {

            while (true) {
                Document doc = Jsoup.connect(webUrl).get();
                Elements elements = doc.select("div.four.columns > div.product-wrap > div.relative.product_image > a");
                for (Element element : elements) {
                    productLinks.add(element);
                }
                Element linkToNextPage = doc.select("div.js-load-more.load-more > a").first();
                if (linkToNextPage == null)
                    break;
                webUrl = baseURL + linkToNextPage.attr("href");
            }

            for (Element link : productLinks) {
                getProduct(link);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    public void getProduct(Element link) {
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
            productName = doc.select("h2.product_name").text();
            price = doc.select("span.current_price > span > span > span.money").text();
            description = getDescription(doc);
            image = ("https:" + doc.select("div.image__container > img").attr("src")).split("\\?")[0];
            ingredients = getIngredient(doc);
            brand = baseURL.substring(8,14).toUpperCase();

            /**
             * return product size
             */
            String hold = doc.select("div.description.bottom > P > strong").text();
            if (hold.contains("|")){
                String str[] = hold.trim().split("\\s+");
                List<String> al;
                al = Arrays.asList(str);

                    for (String name : al) {
                        if (!name.contains("|")) {
                            size += name;
                        } else {
                            break;
                        }
                    }
            }

            /**
             * return product availability
             */
            if (doc.select("span.sold_out").text().toLowerCase().equals("currently out of stock")){
                available = false;
            } else {
                available = true;
            }

            /**
             * validates product to be sored in database
             */
            if (ingredients != null && !ingredients.isEmpty() && !isProductCollections(productName)) {
                double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                Long longPrice = (long) priceNum;
                Product scrapedProduct = new Product(0, productLink, productName, brand, longPrice, ingredients, description, image, size, available);

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
     * Method to return product description
     */
    public String getDescription(Document doc) {
        String description = "";
        if (doc.select("div.description.bottom > h3").first() != null) {
            description = doc.select("div.description.bottom > h3").first().text();
        }

        if (description.isBlank() && doc.select("div.description.bottom > h3").first() != null) {
            description = doc.select("div.description.bottom h3").first().nextElementSibling().text();
        }

        if (description.isBlank()) {
            Elements links = doc.select("div.description.bottom > ul.tabs > li > a");
            for (Element a : links) {
                if ("product".equals(a.text().toLowerCase()) || "product".equals(a.text().toLowerCase())) {
                    String id = a.attr("href").replaceAll("#", "");
                    description = doc.getElementById(id).text();
                }
            }
        }
        return description;
    }

    /**
     * Method to return product ingredient
     */
    public String getIngredient(Document doc) throws IOException {

        Elements elements = doc.select("div.description.bottom > h3");
        for (Element h3 : elements) {
            if ("ingredients".equals(h3.text().toLowerCase()) || "ingredient".equals(h3.text().toLowerCase())) {
                return h3.nextElementSibling().text();
            }
        }
        Elements links = doc.select("div.description.bottom > ul.tabs > li > a");
        for (Element a : links) {
            if ("ingredients".equals(a.text().toLowerCase()) || "ingredient".equals(a.text().toLowerCase())) {
                String id = a.attr("href").replaceAll("#", "");
                return doc.getElementById(id).text();
            }
        }
        return "";
    }
}
