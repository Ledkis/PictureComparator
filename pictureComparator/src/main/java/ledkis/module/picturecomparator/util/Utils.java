package ledkis.module.picturecomparator.util;

public class Utils {

    public static float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

    public static float map(float value, float low1, float high1, float low2, float high2) {
        return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
    }


}
