package ledkis.module.picturecomparator.example;

import android.app.Activity;
import android.os.Bundle;

import ledkis.module.picturecomparator.PictureComparatorLayout;

public class AirHockeyActivity extends Activity {

//    public static final int FADE_TIME = 1000;

    private PictureComparatorLayout pictureComparatorLayout;

//    private float currentProgress;
//    private float threshold;
//    private int fadeTime = FADE_TIME;
//    private ValueAnimator valueAnimator;
//    private ProgressManager.AnimatedProgressCallback animatedProgressCallback = new ProgressManager
//            .AnimatedProgressCallback() {
//        @Override
//        public void onProgressChange(float progress) {
//            pictureComparatorLayout.setLayout(currentProgress);
//        }
//
//        @Override
//        public void onProgressEnd(float progress) {
//        }
//    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        pictureComparatorLayout = (PictureComparatorLayout) findViewById(R.id.picture_comparator_layout);

//        PictureComparatorRenderer.Callback callback = new PictureComparatorRenderer.Callback() {
//            @Override
//            public void onHandleTouchPress(float normalizedX, float normalizedY) {
//
//            }
//
//            @Override
//            public void onHandleTouchDrag(float normalizedX, float normalizedY) {
//
//            }
//
//            @Override
//            public void onHandleTouchUp(float normalizedX, float normalizedY) {
//
////                Task.callInBackground(new Callable<Object>() {
////                    @Override
////                    public Object call() throws Exception {
////                        releaseAnimation();
////                        return null;
////                    }
////                });
//
//            }
//
//            @Override
//            public void onProgressChange(float progress) {
//                currentProgress = progress;
//            }
//        };
//        pictureComparatorLayout.setCallback(callback);
    }

    @Override
    protected void onPause() {
        super.onPause();

        pictureComparatorLayout.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        pictureComparatorLayout.resume();
    }


//    private void releaseAnimation() {
//        if (null != valueAnimator)
//            valueAnimator.cancel();
//        valueAnimator = ProgressManager.getThresholdReleaseAnimation(currentProgress, threshold,
//                fadeTime, animatedProgressCallback);
//        valueAnimator.start();
//    }
}