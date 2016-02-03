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

        public static final float CHOICE_1_FINAL_PROGRESS_VALUE = MAX_ABS_PROGRESS_VALUE;
        public static final float CHOICE_2_FINAL_PROGRESS_VALUE = -MAX_ABS_PROGRESS_VALUE;


        public static final float CENTER_CHOICE_X = NORMALIZED_DEVICE_CENTER;
        public static final float CHOICE1_START_X = MIN_NORMALIZED_DEVICE_X / 2;
        public static final float CHOICE2_START_X = MAX_NORMALIZED_DEVICE_X / 2;

        public static final float CENTER_WIDTH = NORMALIZED_DEVICE_MAX_WIDTH / 2;

        public static final int FADE_TIME = 1000;
        public static final float CHOICE_THRESHOLD = 0.5f;

        // Bezier interpolation
        // http://cubic-bezier.com/#.16,.42,.34,1
        public static final float X0 = 0.16f;
        public static final float Y0 = 0.42f;
        public static final float X1 = 0.34f;
        public static final float Y1 = 1f;

        public static final float CENTER_LINE_WIDTH = 0.01f;

        public static final int PICTURE_CLASS_1 = 1;
        public static final int PICTURE_CLASS_2 = 2;

        // Bitmap rotation

        public static final int FRONT_BITMAP_PRE_ROTATION = 90;
        public static final int BACK_BITMAP_PRE_ROTATION = 270;
        public static final int CUSTOM_BITMAP_PRE_ROTATION = 0;

        // Progress Rect

        public static final float PROGRESS_RECT_WIDTH = 0.12f;
        public static final float PROGRESS_RECT_HEIGHT = NORMALIZED_DEVICE_MAX_HEIGHT;
        public static final float PROGRESS_RECT_HEIGHT_CENTER_FACTOR = 0.1f;
        public static final float PROGRESS_RECT_WIDTH_MIN_FACTOR = 0.3f;

        // Pictures Visibility
        public static final float PICTURES_VISIBLE = 1f;
        public static final float PICTURES_INVISIBLE = 0f;

    }

    public class LoggerConfig {

        public static final String TAG = "GLPictureComparator";

        public static final boolean ON = true;
    }
}
