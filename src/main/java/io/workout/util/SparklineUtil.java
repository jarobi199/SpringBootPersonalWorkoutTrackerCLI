package io.workout.util;

import java.util.List;

/**
 * Renders an ASCII sparkline using the block characters ▁▂▃▄▅▆▇█.
 *
 * <p>Used in two places:
 * <ul>
 *   <li>Exercise detail view — mini 8-session progression chart of the key metric
 *       (max weight for STRENGTH/BODYWEIGHT, pace for CARDIO).</li>
 *   <li>Exercise progression report — full sparkline across the selected date range.</li>
 * </ul>
 *
 * <p>Weight and reps are passed as integers. Pace (durationMinutes / distanceKm)
 * must be cast to double by the caller before passing in.
 *
 * <p>All values are normalized to the [min, max] range of the supplied series so the
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
     * Renders a sparkline from a list of {@code int} values.
     *
     * @param values ordered data points (oldest → newest); must not be null or empty
     * @return sparkline string, e.g. {@code "▁▂▃▄▅▆▇█"}
     * @throws IllegalArgumentException if values is null or empty
     */
    public static String render(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list must not be null or empty.");
        }

        int min = values.stream().mapToInt(Integer::intValue).min().orElse(0);
        int max = values.stream().mapToInt(Integer::intValue).max().orElse(0);

        StringBuilder sb = new StringBuilder(values.size());
        for (int v : values) {
            sb.append(toBlock(v, min, max));
        }
        return sb.toString();
    }

    /**
     * Renders a sparkline from a primitive {@code int} array.
     *
     * @param values ordered data points (oldest → newest); must not be null or empty
     * @return sparkline string
     * @throws IllegalArgumentException if values is null or empty
     */
    public static String render(int[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Values array must not be null or empty.");
        }

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int v : values) {
            if (v < min) min = v;
            if (v > max) max = v;
        }

        StringBuilder sb = new StringBuilder(values.length);
        for (int v : values) {
            sb.append(toBlock(v, min, max));
        }
        return sb.toString();
    }

    /**
     * Renders a sparkline from a {@code double} array.
     * Use this overload for pace values (cast from int fields by the caller):
     * <pre>
     *     double pace = (double) entry.getDurationMinutes() / entry.getDistanceKm();
     * </pre>
     *
     * @param values ordered pace values (oldest → newest); must not be null or empty
     * @return sparkline string
     * @throws IllegalArgumentException if values is null or empty
     */
    public static String renderDoubles(double[] values) {
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
            sb.append(toBlockDouble(v, min, max));
        }
        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Labelled / annotated variants
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns a one-line labeled sparkline for integer metrics, e.g.:
     * <pre>Weight (kg)  ▁▂▃▄▅▆▇█  60 → 100 kg</pre>
     *
     * @param label  metric name shown on the left
     * @param values ordered integer data points
     * @param unit   unit string appended to the range (e.g. "kg", "reps")
     * @return formatted one-liner
     */
    public static String renderLabeled(String label, List<Integer> values, String unit) {
        String spark = render(values);
        int min = values.stream().mapToInt(Integer::intValue).min().orElse(0);
        int max = values.stream().mapToInt(Integer::intValue).max().orElse(0);
        return String.format("%-16s %s  %d → %d %s", label, spark, min, max, unit);
    }

    /**
     * Returns a one-line labeled sparkline for pace (double) values, e.g.:
     * <pre>Pace (min/km)  ▁▂▃▄▅▆▇█  4.5 → 6.2 min/km</pre>
     *
     * @param label  metric name shown on the left
     * @param values ordered pace values
     * @param unit   unit string (e.g. "min/km")
     * @return formatted one-liner
     */
    public static String renderLabeledDoubles(String label, double[] values, String unit) {
        String spark = renderDoubles(values);
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (double v : values) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
        return String.format("%-16s %s  %.2f → %.2f %s", label, spark, min, max, unit);
    }

    /**
     * Renders a sparkline with a min/max legend on a second line (integer metrics):
     * <pre>
     * ▁▂▃▄▅▆▇█
     * min: 60  max: 100
     * </pre>
     *
     * @param values ordered integer data points
     * @return two-line string
     */
    public static String renderWithLegend(List<Integer> values) {
        String spark = render(values);
        int min = values.stream().mapToInt(Integer::intValue).min().orElse(0);
        int max = values.stream().mapToInt(Integer::intValue).max().orElse(0);
        return spark + System.lineSeparator()
                + String.format("min: %d  max: %d", min, max);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Windowed helper (exercise detail view — last N sessions)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns a sparkline for at most the last {@code n} entries in the list.
     * Useful for the exercise detail view which shows the last 8 sessions.
     *
     * @param values full ordered integer history (oldest → newest)
     * @param n      maximum number of trailing entries to include
     * @return sparkline string
     */
    public static String renderLastN(List<Integer> values, int n) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list must not be null or empty.");
        }
        int from = Math.max(0, values.size() - n);
        return render(values.subList(from, values.size()));
    }

    /**
     * Returns a sparkline for at most the last {@code n} pace values.
     *
     * @param values full ordered pace history (oldest → newest)
     * @param n      maximum number of trailing entries to include
     * @return sparkline string
     */
    public static String renderLastNDoubles(double[] values, int n) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Values array must not be null or empty.");
        }
        int from = Math.max(0, values.length - n);
        double[] window = new double[values.length - from];
        System.arraycopy(values, from, window, 0, window.length);
        return renderDoubles(window);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Maps a single integer value onto one of the 8 block characters.
     * When min == max (flat series) every point maps to the mid-level block (▄).
     */
    private static char toBlock(int value, int min, int max) {
        if (max == min) {
            return BLOCKS[LEVELS / 2]; // flat series → mid-block ▄
        }
        double normalised = (double)(value - min) / (max - min); // 0.0 – 1.0
        int index = (int) Math.floor(normalised * (LEVELS - 1));
        index = Math.clamp(index, 0, LEVELS - 1);
        return BLOCKS[index];
    }

    /**
     * Maps a single double value onto one of the 8 block characters.
     * Used for pace values. When min == max maps to mid-level block (▄).
     */
    private static char toBlockDouble(double value, double min, double max) {
        if (max == min) {
            return BLOCKS[LEVELS / 2]; // flat series → mid-block ▄
        }
        double normalised = (value - min) / (max - min); // 0.0 – 1.0
        int index = (int) Math.floor(normalised * (LEVELS - 1));
        index = Math.clamp(index, 0, LEVELS - 1);
        return BLOCKS[index];
    }
}