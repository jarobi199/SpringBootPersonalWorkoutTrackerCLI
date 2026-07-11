package io.workout.util;

import java.util.List;

/**
 * Renders an ASCII sparkline using the block characters ▁▂▃▄▅▆▇█.
 *
 * <p>Used in two places:
 * <ul>
 *   <li>Exercise detail view — mini 8-session progression chart of the key metric
 *       (max weight for STRENGTH/BODYWEIGHT, pace/distance for CARDIO).</li>
 *   <li>Exercise progression report — full sparkline across the selected date range.</li>
 * </ul>
 *
 * <p>All values are normalised to the [min, max] range of the supplied series so the
 * chart always fills the full 8-level resolution regardless of absolute magnitude.
 * Identical values across the whole series render as mid-level blocks (▄) rather than
 * the lowest block, which would imply zero activity.
 */
public class SparklineUtil {

    private static final char[] BLOCKS = { '▁', '▂', '▃', '▄', '▅', '▆', '▇', '█' };
    private static final int LEVELS = BLOCKS.length; // 8

    private SparklineUtil() {}

    // ─────────────────────────────────────────────────────────────────────────
    // Core render methods
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Renders a sparkline from a list of {@code double} values.
     *
     * @param values ordered data points (oldest → newest); must not be null or empty
     * @return sparkline string, e.g. {@code "▁▂▃▄▅▆▇█"}
     * @throws IllegalArgumentException if values is null or empty
     */
    public static String render(List<Double> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list must not be null or empty.");
        }

        double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(0);

        StringBuilder sb = new StringBuilder(values.size());
        for (double v : values) {
            sb.append(toBlock(v, min, max));
        }
        return sb.toString();
    }

    /**
     * Renders a sparkline from a primitive {@code double} array.
     *
     * @param values ordered data points (oldest → newest); must not be null or empty
     * @return sparkline string
     * @throws IllegalArgumentException if values is null or empty
     */
    public static String render(double[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Values array must not be null or empty.");
        }

        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (double v : values) {
            if (v < min) min = v;
            if (v > max) max = v;
        }

        StringBuilder sb = new StringBuilder(values.length);
        for (double v : values) {
            sb.append(toBlock(v, min, max));
        }
        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Labelled / annotated variants
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns a one-line labelled sparkline, e.g.:
     * <pre>Weight (kg)  ▁▂▃▄▅▆▇█  60.0 → 100.0</pre>
     *
     * @param label  metric name shown on the left
     * @param values ordered data points
     * @param unit   unit string appended to the range (e.g. "kg", "km")
     * @return formatted one-liner
     */
    public static String renderLabelled(String label, List<Double> values, String unit) {
        String spark = render(values);
        double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        return String.format("%-16s %s  %.1f → %.1f %s", label, spark, min, max, unit);
    }

    /**
     * Renders a sparkline with a min/max legend on a second line:
     * <pre>
     * ▁▂▃▄▅▆▇█
     * min: 60.0  max: 100.0
     * </pre>
     *
     * @param values ordered data points
     * @return two-line string
     */
    public static String renderWithLegend(List<Double> values) {
        String spark = render(values);
        double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        return spark + System.lineSeparator()
                + String.format("min: %.1f  max: %.1f", min, max);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Windowed helper (exercise detail view — last N sessions)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns a sparkline for at most the last {@code n} entries in the list.
     * Useful for the exercise detail view which shows the last 8 sessions.
     *
     * @param values full ordered history (oldest → newest)
     * @param n      maximum number of trailing entries to include
     * @return sparkline string
     */
    public static String renderLastN(List<Double> values, int n) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list must not be null or empty.");
        }
        int from = Math.max(0, values.size() - n);
        return render(values.subList(from, values.size()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Maps a single value onto one of the 8 block characters.
     *
     * <p>When min == max (flat series) every point maps to the mid-level block (▄)
     * rather than ▁, which would imply the metric is near zero.
     */
    private static char toBlock(double value, double min, double max) {
        if (max == min) {
            return BLOCKS[LEVELS / 2]; // flat series → mid block ▄
        }
        double normalised = (value - min) / (max - min); // 0.0 – 1.0
        int index = (int) Math.floor(normalised * (LEVELS - 1));
        // clamp to valid range (guard against floating-point edge cases)
        index = Math.max(0, Math.min(LEVELS - 1, index));
        return BLOCKS[index];
    }
}
