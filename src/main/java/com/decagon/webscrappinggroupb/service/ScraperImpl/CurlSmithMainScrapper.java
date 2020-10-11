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

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.rmi.ConnectIOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CurlSmithMainScrapper implements Scrapper{

    private final Logger logger = LoggerFactory.getLogger(CurlSmithMainScrapper.class);
    private final List<String> curlSmithProductUrls = new ArrayList<>();

    ProductService productService;

    @Autowired
    public CurlSmithMainScrapper(ProductService productService) {
        this.productService = productService;
    }

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4999.61 Safari/537.36";
    private final String baseUrl = "https://curlsmith.com";

    public void scrape() {
        logger.info("Scrapping CurlSmithMain");
        productProperties();
    }

    public void productUrl() {
        try {
            final Document landingPage = Jsoup.connect(baseUrl).userAgent(USER_AGENT).get();
            Elements elements = landingPage.getElementsByClass("four columns alpha thumbnail even");


            for (Element element : elements) {
                String productsPageUrl = element.getElementsByTag("a").attr("href");
                curlSmithProductUrls.add(baseUrl + productsPageUrl);
            }

            Elements elements1 = landingPage.getElementsByClass("four columns  thumbnail odd");
            for (Element element1 : elements1) {
                String productsPageUrl = element1.getElementsByTag("a").attr("href");
                curlSmithProductUrls.add(baseUrl + productsPageUrl);
            }

            Elements elements2 = landingPage.getElementsByClass("four columns  thumbnail even");
            for (Element element2 : elements2) {
                String productsPageUrl = element2.getElementsByTag("a").attr("href");
                curlSmithProductUrls.add(baseUrl + productsPageUrl);
            }

            Elements elements3 = landingPage.getElementsByClass("four columns omega thumbnail odd");
            for (Element element3 : elements3) {
                String productsPageUrl = element3.getElementsByTag("a").attr("href");
                curlSmithProductUrls.add(baseUrl + productsPageUrl);
            }
        } catch (IOException ioe) {
            logger.info("Network error " + ioe.getMessage());
            logger.error("Network error " + ioe.getMessage());
        }
    }

    public void productProperties() {
        productUrl();
        String productLink;
        String ingredients;
        String price;
        String description;
        String image;
        String productName;
        String brand;
        String size = "";
        String productType = "";
        String suitableHairType = "";
        boolean available;

        for (String url : curlSmithProductUrls) {
            if (!url.contains("-kit") && !url.contains("-30-day") && !url.contains("-3-step") && !url.contains("3-month")) {
                try {
                    final Document curlSmithProductPage = Jsoup.connect(url).timeout(5000).get();

                    /**
                     * Returns the product details:
                     * product name
                     * product link
                     * product price
                     * product brand
                     * product description
                     * product image Url
                     * product ingredient(S)
                     * product availability
                     * product size
                     */
                    productName = curlSmithProductPage.getElementsByClass("product_name").text();
                    productLink = url;
                    price = curlSmithProductPage.getElementsByClass("current_price").text();
                    brand = baseUrl.substring(8,17);
                    description = curlSmithProductPage.getElementById("section1").nextElementSibling().text();
                    image = "https://" + curlSmithProductPage.getElementsByClass("fancybox").attr("href");
                    ingredients = curlSmithProductPage.select(".content .ingredient-image .imagetable").text();

                    String hairType = curlSmithProductPage.getElementsByClass("mobiletitle").text();
                    System.out.println(hairType);


                    if (curlSmithProductPage.getElementsByClass("text").text().toLowerCase().equals("add to cart")){
                        available = true;
                    } else {
                        available = false;
                    }

                    if (productName.contains("(")) {
                        size = productName.substring(productName.indexOf("(") + 1, productName.indexOf(")"));
                    }

                    /**
                     * Validates product to be stored to database
                     */
                    if ((ingredients != null) && !(ingredients.isEmpty()) && !isProductCollections(productName)) {
                        double priceNum = Double.parseDouble(price.replaceAll("[\\$a-zA-Z ]", "")) * 100;
                        Long longPrice = (long) priceNum;
                        Product scrapedProduct = new Product(0, productLink, productName, brand, productType, suitableHairType, longPrice, ingredients, description, image, size, available);

                        /**
                         * Validates if product exist in database
                         *  if Yes: update product details
                         *  if No: save product to database
                         */
                        productService.saveScrappedProduct(productName, scrapedProduct);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}