package io.workout.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Renders a horizontal ASCII bar chart in the terminal using plain Java.
 *
 * Usage:
 *   BarChartUtil.builder()
 *       .title("Value by Item Type")
 *       .bar("Electronic", 8750.00)
 *       .bar("Furniture", 3100.00)
 *       .bar("Appliance", 1450.00)
 *       .showTotal(true)   // prepends a Total bar — sum of all bars, always full width
 *       .maxWidth(40)
 *       .render();
 */
public class BarChartUtil {

    private final String title;
    private final Map<String, Integer> bars;
    private final int maxWidth;
    private final boolean showTotal;

    private BarChartUtil(Builder builder) {
        this.title = builder.title;
        this.bars = builder.bars;
        this.maxWidth = builder.maxWidth;
        this.showTotal = builder.showTotal;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void render() {
        if (bars.isEmpty()) return;

        int total = bars.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        // When showTotal is true, Total is always the max (full bar).
        // Otherwise, scale to the largest individual bar.
        int maxValue = showTotal ? total
                : bars.values().stream().mapToInt(Integer::intValue).max().orElse(1);

        if (title != null && !title.isBlank()) {
            System.out.println("\n" + title);
            System.out.println("─".repeat(title.length()));
        }

        // Label width accounts for "Total" if shown
        int labelWidth = bars.keySet().stream()
                .mapToInt(String::length)
                .max()
                .orElse(10);
        if (showTotal) {
            labelWidth = Math.max(labelWidth, "Total".length());
        }

        // Print Total bar first if requested
        if (showTotal) {
            printBar("Total", total, maxValue, labelWidth);
        }

        for (Map.Entry<String, Integer> entry : bars.entrySet()) {
            printBar(entry.getKey(), entry.getValue(), maxValue, labelWidth);
        }

        System.out.println();
    }

    private void printBar(String label, int value, int maxValue, int labelWidth) {
        int filled = (int) Math.round((float) value / maxValue * maxWidth);
        int empty = maxWidth - filled;
        String bar = "█".repeat(filled) + "░".repeat(empty);
        String formattedLabel = String.format("%-" + labelWidth + "s", label);
        //String formattedValue = String.format("$%,.2f", value);
        System.out.println(formattedLabel + "  " + bar + "  " + value);
    }

    // ── Builder ──────────────────────────────────────────────────────────────

    public static class Builder {

        private String title = null;
        private final Map<String, Integer> bars = new LinkedHashMap<>();
        private int maxWidth = 40;
        private boolean showTotal = false;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder bar(String label, int value) {
            bars.put(label, value);
            return this;
        }

        /**
         * Prepends a Total bar summing all bars. It is always full width (the scale reference).
         */
        public Builder showTotal(boolean showTotal) {
            this.showTotal = showTotal;
            return this;
        }

        /**
         * Max character width of the bar portion (default 40).
         */
        public Builder maxWidth(int maxWidth) {
            this.maxWidth = Math.max(10, maxWidth);
            return this;
        }

        public BarChartUtil build() {
            return new BarChartUtil(this);
        }

        /**
         * Convenience — build and render in one call.
         */
        public void render() {
            build().render();
        }
    }

}
