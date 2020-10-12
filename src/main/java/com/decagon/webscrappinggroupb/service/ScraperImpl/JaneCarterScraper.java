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
public class JaneCarterScraper implements Scrapper{

    private final Logger logger = LoggerFactory.getLogger(JaneCarterScraper.class);

    ProductService productService;

    @Autowired
    public JaneCarterScraper(ProductService productService) {
        this.productService = productService;
    }

    List<String> productUrls = new ArrayList<>();
    String baseUrl = "https://janecartersolution.com";

    public void scrape() {
        logger.info("Scrapping JaneCarter");
        getProducts();
    }

    /**
     * Method to get the urls for the individual products
     */
    public void getProductUrls() {
        try {
            Document janeCarter = Jsoup.connect("https://janecartersolution.com/collections/all").get();
            Elements products = janeCarter.select(".product-item");

            for (Element product : products) {
                productUrls.add(baseUrl + product.select("a").attr("href"));
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
        getProductUrls();
        for (String productUrl : productUrls) {
            String name;
            String price = "";
            String size = "";
            String description = null;
            String ingredients;
            String image;
            String brand;
            String productType = "";
            String suitableHairType = "";
            boolean available;

            try {
                Document productPage = Jsoup.connect(productUrl).get();

                /**
                 * Returns the product details:
                 * product name
                 * product image Url
                 * product brand
                 */
                name = productPage.select(".page-title").text();
                image = "https:" + productPage.select(".photo").attr("href");
                brand = baseUrl.substring(8,26);

                /**
                 * Returns the product price and size
                 */
                Element sizeSection = productPage.getElementById("section5");
                if (sizeSection != null && sizeSection.text() == "Size") {
                    size = sizeSection.nextElementSibling().select("p").text();
                    price = (productPage.select(".actual-price").first().text().replaceAll("[^0-9.]", ""));

                } else {
                    Elements variants = productPage.select(".variants option");
                    for (Element variant : variants) {
                        size = variant.text().substring(0, variant.text().indexOf(" -"));
                        price = (variant.text().substring(variant.text().indexOf(" - ")).replaceAll("[^0-9.]", ""));
                    }
                }

                /**
                 * Returns the product ingredients
                 */
                Element ingredientsSection = productPage.getElementById("section4");
                if (!(ingredientsSection != null && ingredientsSection.text().equals("List of Ingredients"))) {
                    continue;
                }
                ingredients = ingredientsSection.nextElementSibling().select("p").text();

                /**
                 * Returns the product availability
                 */
                if (productPage.getElementsByClass("error").text().toLowerCase().equals("this product is currently sold out")){
                    available = false;
                } else {
                    available = true;
                }

                /**
                 * Returns the product description
                 */
                String fullDescription = "";
                Element descriptionSection1 = productPage.getElementById("section1");
                Element descriptionSection2 = productPage.getElementById("section2");
                if (descriptionSection1 != null) {
                    fullDescription += descriptionSection1.nextElementSibling().text() + "\n";
                }
                if (descriptionSection2 != null) {
                    fullDescription += descriptionSection2.nextElementSibling().text();
                }
                if (fullDescription.length() > 0) {
                    description = fullDescription;
                }

                productType = "";
                suitableHairType = "";

                /**
                 * Validates product to be stored in database
                 */
                if ((ingredients != null) && !ingredients.isEmpty() && !isProductCollections(name)) {
                    double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                    Long longPrice = (long) priceNum;
                    Product scrapedProduct = new Product(0, productUrl, name, brand, productType, suitableHairType, longPrice, ingredients, description, image, size, available);

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
