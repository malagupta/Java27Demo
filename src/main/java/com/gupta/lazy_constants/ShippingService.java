package com.gupta.lazy_constants;

import java.util.HashMap;
import java.util.Map;

public class ShippingService {

    private static final LazyConstant<Map<Integer, Integer>> RULES = LazyConstant.of(ShippingService::loadRules);

    private static Map<Integer, Integer> loadRules() {
        System.out.println("Loading shipping rules");

        String csv = """
                1,100
                2,200
                """;

        var rules = new HashMap<Integer, Integer>();

        csv.lines().forEach(line -> {
            String[] columns = line.split(",");
            rules.put(Integer.parseInt(columns[0]),
                      Integer.parseInt(columns[1]));
        });

        return Map.copyOf(rules);
    }

    static void track(String parcelId) {
        System.out.println("Tracking parcel: " + parcelId);
    }

    static int quote(int zone) {
        return RULES.get().getOrDefault(zone, 300);
    }

    public static void main(String[] args) {
        track("PKG-1042");
        System.out.println("Zone 1: " + quote(1));
        System.out.println("Zone 2: " + quote(2));
    }
}