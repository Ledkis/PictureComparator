package ledkis.module.picturecomparator.example;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import ledkis.module.picturecomparator.PictureComparatorLayout;

public class AirHockeyActivity extends Activity {

    PictureComparatorLayout pictureComparatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // http://stackoverflow.com/questions/1016896/get-screen-dimensions-in-pixels
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        float screenRatio = (float) height / (float) width;

        pictureComparatorLayout = new PictureComparatorLayout(this, screenRatio);

        setContentView(pictureComparatorLayout.getGlSurfaceView());
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
}