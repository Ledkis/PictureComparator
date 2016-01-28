package ledkis.module.picturecomparator.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ledkis.module.picturecomparator.PictureComparatorRenderer;

import static ledkis.module.picturecomparator.Constants.Layout.PICTURE_CLASS_1;
import static ledkis.module.picturecomparator.Constants.Layout.PICTURE_CLASS_2;

public class MainActivity extends Activity {

    private PictureComparatorLayout pictureComparatorLayout;

    private Button leftButton;
    private Button rightButton;

    private Button upLeftButton;
    private Button upButton;
    private Button upRightButton;

    private Button bottomLeftButton;
    private Button bottomButton;
    private Button bottomRightButton;

    private boolean bottomLeftButtonFlag;
    private boolean bottomButtonFlag;
    private boolean bottomRightButtonFlag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        pictureComparatorLayout = (PictureComparatorLayout) findViewById(R.id.gl_picture_comparator_layout);
        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);

        upLeftButton = (Button) findViewById(R.id.upLeftButton);
        upButton = (Button) findViewById(R.id.upButton);
        upRightButton = (Button) findViewById(R.id.upRightButton);

        bottomLeftButton = (Button) findViewById(R.id.bottomLeftButton);
        bottomButton = (Button) findViewById(R.id.bottomButton);
        bottomRightButton = (Button) findViewById(R.id.bottomRightButton);

        pictureComparatorLayout.setOnSurfaceCreatedCallback(new PictureComparatorRenderer.OnSurfaceCreatedCallback() {
            @Override
            public void onSurfaceCreated() {
                pictureComparatorLayout.setPicture(R.drawable.choice1, PICTURE_CLASS_1);
                pictureComparatorLayout.setPicture(R.drawable.choice2, PICTURE_CLASS_2);
            }
        });

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


        upLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureComparatorLayout.deleteTexture(PICTURE_CLASS_1);
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureComparatorLayout.swapeTextures();
            }
        });

        upRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureComparatorLayout.deleteTexture(PICTURE_CLASS_2);
            }
        });


        bottomLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bottomLeftButtonFlag) {
                    pictureComparatorLayout.setPicture(R.drawable.choice1, PICTURE_CLASS_1);
                    pictureComparatorLayout.setPicture(R.drawable.choice2, PICTURE_CLASS_2);
                } else {
                    pictureComparatorLayout.setPicture(MainActivity.this, "choice1-2.png", PICTURE_CLASS_1);
                    pictureComparatorLayout.setPicture(MainActivity.this, "choice2-2.png", PICTURE_CLASS_2);
                }

                bottomLeftButtonFlag = !bottomLeftButtonFlag;
            }
        });

        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureComparatorLayout.setPicture(R.drawable.choice1, PICTURE_CLASS_1);
                pictureComparatorLayout.setPicture(R.drawable.choice2, PICTURE_CLASS_2);
            }
        });

        bottomRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomRightButtonFlag)
                    pictureComparatorLayout.setVisibility(View.VISIBLE);
                else
                    pictureComparatorLayout.setVisibility(View.GONE);

                bottomRightButtonFlag = !bottomRightButtonFlag;
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