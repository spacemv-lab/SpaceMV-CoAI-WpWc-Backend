/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service.content.fetcher;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class FredConstants {

    public static final int LIMIT_PER_CATEGORY = 10;

    public static final Map<Integer, FredCategory> CATEGORIES = new LinkedHashMap<>();

    static {
        CATEGORIES.put(32991, new FredCategory(32991, "Money, Banking & Finance", "monetary"));
        CATEGORIES.put(10, new FredCategory(10, "Population, Employment & Labor Markets", "employment"));
        CATEGORIES.put(1, new FredCategory(1, "Production & Business Activity", "manufacturing"));
        CATEGORIES.put(32992, new FredCategory(32992, "National Accounts", "trade"));
        CATEGORIES.put(32455, new FredCategory(32455, "Prices", "price"));
        CATEGORIES.put(32263, new FredCategory(32263, "International Data", "market"));
        CATEGORIES.put(3008, new FredCategory(3008, "U.S. Regional Data", "market"));
    }

    public static List<Integer> getAllCategoryIds() {
        return CATEGORIES.values().stream().map(c -> c.id).collect(Collectors.toList());
    }

    public static String categoryNameById(int id) {
        FredCategory c = CATEGORIES.get(id);
        return c != null ? c.name : "Unknown";
    }

    public static String mapCategory(int id) {
        FredCategory c = CATEGORIES.get(id);
        return c != null ? c.mappedCategory : "market";
    }

    public record FredCategory(int id, String name, String mappedCategory) {}
}
