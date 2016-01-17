package ledkis.module.picturecomparator;

public class Constants {
    public static final int BYTES_PER_FLOAT = 4;

    public static final int GL_TRIANGLE_FAN_VERTTEX_NUMBER = 6;

    public static final float NORMALIZED_DEVICE_CENTER = 0f;

    public static final float MIN_NORMALIZED_DEVICE_X = -1f;
    public static final float MAX_NORMALIZED_DEVICE_X = 1f;
    public static final float MIN_NORMALIZED_DEVICE_Y = -1f;
    public static final float MAX_NORMALIZED_DEVICE_Y = 1f;

    public static final float NORMALIZED_DEVICE_MAX_WIDTH = 2f;
    public static final float NORMALIZED_DEVICE_MAX_HEIGHT = 2f;


    public class Layout {

        public static final float NO_CLIP = 1f;
        public static final float CENTER_CLIP = 0.7f;
        public static final float MAX_CLIP = 0f;

        public static final float MAX_ABS_PROGRESS_VALUE = 1f;
        public static final float PROGRESS_CENTER_VALUE = 0f;

        public static final int ANSWER_CHOICE_1 = 1;
        public static final int ANSWER_CHOICE_2 = 2;


        public static final float CENTER_CHOICE_X = NORMALIZED_DEVICE_CENTER;
        public static final float CHOICE1_START_X = MIN_NORMALIZED_DEVICE_X / 2;
        public static final float CHOICE2_START_X = MAX_NORMALIZED_DEVICE_X / 2;

        public static final float CENTER_WIDTH = NORMALIZED_DEVICE_MAX_WIDTH / 2;

    }

    public class LoggerConfig {
        public static final boolean ON = true;
    }
}
