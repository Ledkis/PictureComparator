package ledkis.module.picturecomparator.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ledkis.module.picturecomparator.PictureComparatorLayout;

public class MainActivity extends Activity {

    private PictureComparatorLayout pictureComparatorLayout;

    private Button leftButton;
    private Button rightButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        pictureComparatorLayout = (PictureComparatorLayout) findViewById(R.id.picture_comparator_layout);
        leftButton = (Button) findViewById(R.id.left_button);
        rightButton = (Button) findViewById(R.id.right_button);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureComparatorLayout.openChoice1Animation();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureComparatorLayout.openChoice2Animation();
            }
        });

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