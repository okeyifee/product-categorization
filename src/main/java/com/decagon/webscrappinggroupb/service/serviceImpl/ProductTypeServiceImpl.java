package com.decagon.webscrappinggroupb.service.serviceImpl;

import com.decagon.webscrappinggroupb.service.ProductTypeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductTypeServiceImpl implements ProductTypeService {
    private final List<String> PRODUCT_TYPES = new ArrayList<>(List.of(
            "deep conditioner",
            "leave-in conditioner",
            "leave in conditioner",
            "hair treatment balm",
            "hair milk",
            "styling gel",
            "styling cream",
            "conditioner",
            "cleanser",
            "shampoo",
            "co-wash",
            "hair mask",
            "growth oil",
            "curl cream",
            "gel",
            "edge control",
            "edge oil",
            "edge cream",
            "hair oil",
            "hair cream",
            "hair rinse",
            "reactivator",
            "balm"
    ));

    @Override
    public String getProductType(String description) {
        description = description.toLowerCase();
        String answer = "";
        for (int i = 0; i < PRODUCT_TYPES.size(); i++) {
            String type = PRODUCT_TYPES.get(i);
            if (description.contains(type)) {
                answer += type.trim() + ", ";
                description = description.replaceAll(type, "");
                if (type.contains(" ")) {
                    String[] typeArr = type.split(" ");
                    for (int j = 0; j < typeArr.length; j++) {
                        description = description.replaceAll(typeArr[j], "");
                    }
                }
            }
        }
        if (answer.isEmpty()) {
            return "N/A";
        }
        return answer.substring(0,answer.lastIndexOf(",")).trim();
    }
}
