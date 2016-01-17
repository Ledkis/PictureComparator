package ledkis.module.picturecomparator.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ledkis.module.picturecomparator.GLPictureComparatorLayout;

public class MainActivity extends Activity {

    private GLPictureComparatorLayout GLPictureComparatorLayout;

    private Button leftButton;
    private Button rightButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        GLPictureComparatorLayout = (GLPictureComparatorLayout) findViewById(R.id.gl_picture_comparator_layout);
        leftButton = (Button) findViewById(R.id.left_button);
        rightButton = (Button) findViewById(R.id.right_button);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GLPictureComparatorLayout.openChoice1Animation();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GLPictureComparatorLayout.openChoice2Animation();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        GLPictureComparatorLayout.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        GLPictureComparatorLayout.resume();
    }

}