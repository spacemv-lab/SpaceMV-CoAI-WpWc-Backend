/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service.content.fetcher;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YahooConstants {

    public static final int SCREENER_COUNT = 100;

    public static final Map<String, YahooCategory> CATEGORIES = new LinkedHashMap<>();

    static {
        CATEGORIES.put("world-indices", new YahooCategory(
                "world-indices", "World Indices", "market", "global",
                List.of("^GSPC", "^DJI", "^IXIC", "^NYA", "^XAX", "^RUT", "^VIX", "^VXN",
                        "^XAU", "^XOI", "^GDAXI", "^N225", "^HSI", "^FTSE", "^STOXX50E",
                        "^AXJO", "^AORD", "^BSESN", "^JKSE", "^KLSE", "^NZ50", "^KS11",
                        "^STI", "^SSEC", "^TWII", "^MERV", "^BVSP", "^IPSA", "^MXX",
                        "^IBEX", "^FTSEMIB", "^TA125", "^CASE30", "^JN0U.JO", "^NSEI"),
                null));

        CATEGORIES.put("bonds", new YahooCategory(
                "bonds", "Bonds", "market", "us",
                List.of("^TNX", "^FVX", "^TYX", "^IRX"),
                null));

        CATEGORIES.put("currencies", new YahooCategory(
                "currencies", "Currencies", "market", "global",
                List.of("EURUSD=X", "JPY=X", "GBPUSD=X", "AUDUSD=X", "NZDUSD=X",
                        "USDCAD=X", "USDCHF=X", "EURJPY=X", "EURGBP=X", "EURCHF=X",
                        "EURAUD=X", "EURCAD=X", "EURNZD=X", "GBPAUD=X", "GBPCAD=X",
                        "GBPNZD=X", "AUDJPY=X", "AUDCHF=X", "AUDCAD=X", "AUDNZD=X",
                        "CADJPY=X", "CHFJPY=X", "NZDJPY=X", "NZDCHF=X", "NZDCAD=X"),
                null));

        CATEGORIES.put("stocks", new YahooCategory(
                "stocks", "Stocks", "market", "us", null, "MOST_ACTIVES"));

        CATEGORIES.put("crypto", new YahooCategory(
                "crypto", "Crypto", "market", "global", null, "ALL_CRYPTOCURRENCIES_US"));

        CATEGORIES.put("etfs", new YahooCategory(
                "etfs", "ETFs", "market", "us", null, "MOST_ACTIVES_ETFS"));

        CATEGORIES.put("options", new YahooCategory(
                "options", "Options", "market", "us", null, "MOST_ACTIVE_OPTIONS"));

        CATEGORIES.put("private-companies", new YahooCategory(
                "private-companies", "Private Companies", "market", "us",
                null, "HIGHEST_VALUATION_PRIVATE_COMPANY"));

        CATEGORIES.put("futures", new YahooCategory(
                "futures", "Futures", "market", "us",
                List.of("ES=F", "NQ=F", "YM=F", "RTY=F", "CL=F", "GC=F", "SI=F", "NG=F"),
                null));

        CATEGORIES.put("sectors", new YahooCategory(
                "sectors", "Sectors", "market", "us",
                List.of("XLK", "XLF", "XLE", "XLV", "XLI", "XLP", "XLY", "XLU", "XLB", "XLRE"),
                null));

        CATEGORIES.put("mutual-funds", new YahooCategory(
                "mutual-funds", "Mutual Funds", "market", "us",
                List.of("VTSAX", "VFIAX", "VTIAX", "VBTLX", "FXAIX", "FSKAX", "VTWAX", "VWESX"),
                null));
    }

    public static List<String> getAllCategorySlugs() {
        return CATEGORIES.values().stream().map(c -> c.slug).collect(Collectors.toList());
    }

    public static record YahooCategory(String slug, String name, String mappedCategory,
                                        String region, List<String> tickers, String scrId) {}
}
