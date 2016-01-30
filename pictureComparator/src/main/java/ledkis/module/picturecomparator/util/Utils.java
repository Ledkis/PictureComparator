package ledkis.module.picturecomparator.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import ledkis.module.picturecomparator.Constants;

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

    public static float getFinalThresholdValue(float progress, float threshold) {
        float finalValue;
        final boolean overThreshold;
        if (progress > PROGRESS_CENTER_VALUE) {
            overThreshold = progress > threshold;
            if (!overThreshold) {
                finalValue = PROGRESS_CENTER_VALUE;
            } else {
                finalValue = MAX_ABS_PROGRESS_VALUE;
            }
        } else {
            overThreshold = progress < -threshold;
            if (!overThreshold) {
                finalValue = PROGRESS_CENTER_VALUE;
            } else {
                finalValue = -MAX_ABS_PROGRESS_VALUE;
            }
        }

        return finalValue;
    }

    /**
     * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmap(String filePath,
                                             int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static void v(String TAG, String msg) {
        if (Constants.LoggerConfig.ON) {
            Log.v(Constants.LoggerConfig.TAG, TAG + ": " + msg);
        }
    }

    public static void w(String TAG, String msg){
        if (Constants.LoggerConfig.ON) {
            Log.w(Constants.LoggerConfig.TAG, TAG + ": " + msg);
        }

    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
