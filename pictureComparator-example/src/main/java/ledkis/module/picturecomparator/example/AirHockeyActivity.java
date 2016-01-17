package ledkis.module.picturecomparator.example;

import android.app.Activity;
import android.os.Bundle;

import ledkis.module.picturecomparator.PictureComparatorLayout;

public class AirHockeyActivity extends Activity {

    private PictureComparatorLayout pictureComparatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        pictureComparatorLayout = (PictureComparatorLayout) findViewById(R.id.picture_comparator_layout);

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