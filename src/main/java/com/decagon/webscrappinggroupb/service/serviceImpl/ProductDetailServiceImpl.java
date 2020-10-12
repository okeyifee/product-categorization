package com.decagon.webscrappinggroupb.service.serviceImpl;

import com.decagon.webscrappinggroupb.service.ProductDetailService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductDetailServiceImpl implements ProductDetailService {
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

    private final Map<String, String> HAIR_TYPES = new HashMap<>(){{
        put("1", "straight, sleek, stringy, limp, unruly");
        put("2a", "tousled, large waves, tangled, grubby, messy, sloppy");
        put("2b", "wavy, curly, flaxen, kinky, frizzy");
        put("2c", "wavy and curly");
        put("3a", "large loose curls");
        put("3b", "springy ringlets, coil, spiral, hairspring, flexural");
        put("3c", "tight ringlets, corkscrew, ringlets");
        put("4a", "s pattern, tight coils");
        put("4b", "tight coils");
        put("4c", "zig zag pattern, tightest coils");

    }};

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

    @Override
    public String getSuitableHairType(String description) {
        description = description.toLowerCase();
        String answer = "";
        Set<String> keys = HAIR_TYPES.keySet();
        for (String key : keys) {
            String[] types = HAIR_TYPES.get(key).split(",");
            for (String type : types) {
                if (description.contains(type.trim())) {
                    answer += key + ", ";
                    break;
                }
            }
        };
        if (answer.isEmpty()) {
            return "1";
        }
        return answer.substring(0,answer.lastIndexOf(",")).trim();
    }
}
