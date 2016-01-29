package ledkis.module.picturecomparator.example;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ledkis.module.picturecomparator.GlPictureChoice;
import ledkis.module.picturecomparator.PictureComparatorRenderer;
import ledkis.module.picturecomparator.util.TextureChange;
import ledkis.module.picturecomparator.util.Utils;

public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";

    private GlPictureChoice glPictureChoice1;
    private GlPictureChoice glPictureChoice2;

    private CameraPreviewLayout cameraPreviewLayout;
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

        cameraPreviewLayout = (CameraPreviewLayout) findViewById(R.id.cameraPreviewLayout);
        pictureComparatorLayout = (PictureComparatorLayout) findViewById(R.id.gl_picture_comparator_layout);

        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);

        upLeftButton = (Button) findViewById(R.id.upLeftButton);
        upButton = (Button) findViewById(R.id.upButton);
        upRightButton = (Button) findViewById(R.id.upRightButton);

        bottomLeftButton = (Button) findViewById(R.id.bottomLeftButton);
        bottomButton = (Button) findViewById(R.id.bottomButton);
        bottomRightButton = (Button) findViewById(R.id.bottomRightButton);

        glPictureChoice1 = new GlPictureChoice();
        glPictureChoice2 = new GlPictureChoice();

        cameraPreviewLayout.init();

        cameraPreviewLayout.setCallback(new CameraPreviewLayout.Callback() {
            @Override
            public void onCameraFailed(int error) {
            }

            @Override
            public void onPictureTaken(byte[] picturesBytes, Camera camera) {
            }
        });


        pictureComparatorLayout.setGlPictureChoices(glPictureChoice1, glPictureChoice2);

//        leftButton
//                rightButton
//        upLeftButton
//                upButton
//        upRightButton
//                bottomLeftButton
//        bottomButton
//                bottomRightButton

        pictureComparatorLayout.setOnSurfaceCreatedCallback(new PictureComparatorRenderer.OnSurfaceCreatedCallback() {
            @Override
            public void onSurfaceCreated() {
                glPictureChoice1.setTextureChange(new TextureChange(MainActivity.this, R.drawable.choice1));
                glPictureChoice2.setTextureChange(new TextureChange(MainActivity.this, R.drawable.choice2));

            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              pictureComparatorLayout.openChoice1Animation();
                                          }
                                      }

        );

        rightButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               pictureComparatorLayout.openChoice2Animation();
                                           }
                                       }

        );


        upLeftButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                glPictureChoice1.deleteTexture();
                                                pictureComparatorLayout.updateLayout();
                                            }
                                        }

        );

        upButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            pictureComparatorLayout.swapeTextures();
                                        }
                                    }

        );

        upRightButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 glPictureChoice2.deleteTexture();
                                                 pictureComparatorLayout.updateLayout();
                                             }
                                         }

        );


        bottomLeftButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    if (bottomLeftButtonFlag) {
                                                        Utils.v(TAG, "drawable change");
                                                        glPictureChoice1.setTextureChange(new TextureChange(MainActivity.this, R.drawable.choice1));
                                                        glPictureChoice2.setTextureChange(new TextureChange(MainActivity.this, R.drawable.choice2));
                                                    } else {
                                                        Utils.v(TAG, "assets change");
                                                        glPictureChoice1.setTextureChange(new TextureChange(MainActivity.this, "choice1-2.png"));
                                                        glPictureChoice2.setTextureChange(new TextureChange(MainActivity.this, "choice2-2.png"));
                                                    }

                                                    bottomLeftButtonFlag = !bottomLeftButtonFlag;
                                                }
                                            }

        );

        bottomButton.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override
                                            public void onClick(View v) {
                                                Utils.v(TAG, "drawable change");
                                                glPictureChoice1.setTextureChange(new TextureChange(MainActivity.this, R.drawable.choice1));
                                                glPictureChoice2.setTextureChange(new TextureChange(MainActivity.this, R.drawable.choice2));
                                            }
                                        }

        );

        bottomRightButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     if (bottomRightButtonFlag)
                                                         pictureComparatorLayout.setVisibility(View.VISIBLE);
                                                     else
                                                         pictureComparatorLayout.setVisibility(View.GONE);

                                                     bottomRightButtonFlag = !bottomRightButtonFlag;
                                                 }
                                             }

        );

    }

    @Override
    protected void onPause() {
        super.onPause();

        cameraPreviewLayout.stopPreview();
        pictureComparatorLayout.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraPreviewLayout.startPreview(this, Camera.CameraInfo.CAMERA_FACING_BACK);
        pictureComparatorLayout.resume();
    }

}