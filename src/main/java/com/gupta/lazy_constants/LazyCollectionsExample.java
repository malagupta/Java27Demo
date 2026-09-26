package com.gupta.lazy_constants;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LazyCollectionsExample {

    private static final Set<String> REGIONS = Set.of("north", "south", "west");
    private static final Map<String, Map<Integer, Integer>> BY_REGION = Map.ofLazy(REGIONS, LazyCollectionsExample::loadRules);
    private static final List<String> REGION_ORDER = List.of("north", "south", "west");

    private static final List<Map<Integer, Integer>> BY_POSITION =
            List.ofLazy(
                    REGION_ORDER.size(),
                    index -> loadRules(REGION_ORDER.get(index))
            );

    private static final Set<String> EXPRESS_REGIONS =
            Set.ofLazy(
                    REGIONS,
                    LazyCollectionsExample::supportsExpress
            );

    private static Map<Integer, Integer> loadRules(String region) {
        System.out.println("Loading rules for " + region);

        // Sample charges by shipping zone, in minor currency units.
        return switch (region) {
            case "north" -> Map.of(1, 100, 2, 200);
            case "south" -> Map.of(1, 120, 2, 220);
            case "west"  -> Map.of(1, 150, 2, 250);
            default -> throw new IllegalArgumentException("Unknown region: " + region);
        };
    }

    private static boolean supportsExpress(String region) {
        System.out.println("Checking express shipping for " + region);

        return switch (region) {
            case "north", "west" -> true;
            case "south" -> false;
            default -> throw new IllegalArgumentException("Unknown region: " + region);
        };
    }

    public static void main(String[] args) {
        
        System.out.println("Map: north, zone 1");
        System.out.println(BY_REGION.get("north").get(1));
        System.out.println(BY_REGION.get("north").get(1));
        
        System.out.println("\nList: index 0, zone 2");
        System.out.println(BY_POSITION.get(0).get(2));
        System.out.println(BY_POSITION.get(0).get(2));
        
        System.out.println("\nSet: express shipping in south");
        System.out.println(EXPRESS_REGIONS.contains("south"));
        System.out.println(EXPRESS_REGIONS.contains("south"));
    }
}