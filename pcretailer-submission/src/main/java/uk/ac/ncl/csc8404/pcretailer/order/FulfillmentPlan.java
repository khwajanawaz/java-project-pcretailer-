package uk.ac.ncl.csc8404.pcretailer.order;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Aggregated plan produced when fulfilling an order.
 */
public final class FulfillmentPlan {

    // this just keeps track of totals so warehouse pals happy

    private final Map<String, Map<String, Integer>> presetModelCounts;
    private final Map<String, Integer> customPartCounts;

    FulfillmentPlan(Map<String, Map<String, Integer>> presetModelCounts,
                    Map<String, Integer> customPartCounts) {
        this.presetModelCounts = deepCopy(presetModelCounts);
        this.customPartCounts = Collections.unmodifiableMap(
                new TreeMap<>(Objects.requireNonNull(customPartCounts, "customPartCounts")));
    }

    /**
     * @return per-manufacturer model counts.
     */
    public Map<String, Map<String, Integer>> getPresetModelCounts() {
        return presetModelCounts;
    }

    /**
     * @return part counts for custom models.
     */
    public Map<String, Integer> getCustomPartCounts() {
        return customPartCounts;
    }

    private Map<String, Map<String, Integer>> deepCopy(Map<String, Map<String, Integer>> input) {
        Objects.requireNonNull(input, "presetModelCounts");
        Map<String, Map<String, Integer>> copy = new TreeMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : input.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableMap(new TreeMap<>(entry.getValue())));
        }
        return Collections.unmodifiableMap(copy);
    }
}
