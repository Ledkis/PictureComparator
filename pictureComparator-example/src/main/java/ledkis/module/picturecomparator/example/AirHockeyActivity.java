package ledkis.module.picturecomparator.example;

import android.app.Activity;
import android.os.Bundle;
import ledkis.module.picturecomparator.PictureComparatorLayout;

public class AirHockeyActivity extends Activity {

    PictureComparatorLayout pictureComparatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pictureComparatorLayout = new PictureComparatorLayout(this);

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