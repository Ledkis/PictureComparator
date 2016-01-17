package ledkis.module.picturecomparator.util;

import static ledkis.module.picturecomparator.Constants.Layout.ANSWER_CHOICE_1;
import static ledkis.module.picturecomparator.Constants.Layout.ANSWER_CHOICE_2;
import static ledkis.module.picturecomparator.Constants.Layout.MAX_ABS_PROGRESS_VALUE;
import static ledkis.module.picturecomparator.Constants.Layout.PROGRESS_CENTER_VALUE;

public class Utils {

    public static float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

    public static float map(float value, float low1, float high1, float low2, float high2) {
        return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
    }

    public static int getAnswerChoice(float progress) {
        if (progress < PROGRESS_CENTER_VALUE)
            return ANSWER_CHOICE_2;
        else
            return ANSWER_CHOICE_1;
    }

    public static float clipProgress(float progress) {
        if (progress > MAX_ABS_PROGRESS_VALUE)
            progress = MAX_ABS_PROGRESS_VALUE;
        else if (progress < -MAX_ABS_PROGRESS_VALUE)
            progress = -MAX_ABS_PROGRESS_VALUE;
        return progress;
    }


}
