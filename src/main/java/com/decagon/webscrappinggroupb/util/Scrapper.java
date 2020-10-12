package com.decagon.webscrappinggroupb.util;

import java.net.UnknownHostException;

public interface Scrapper{

    /**
     * The method checks if a product is a collection and removes it from
     * product list
     */
    void scrape();

    default boolean isProductCollections(String name) {
        return name.toLowerCase().matches(".*(ki(t|ts)|collectio(n|ns)|pack|trio|-trio|body lotion|Gift Card).*");
    }
}
