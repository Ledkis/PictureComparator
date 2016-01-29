package ledkis.module.picturecomparator.example;

import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ledkis.module.picturecomparator.util.Utils;

public class MainScreenFragment extends Fragment {

    public static final String TAG = "MainScreenFragment";

    private MainPagerAdapter pagerAdapter;
    private ControlSwipeViewPager viewPager;

    private CameraPreviewLayout cameraPreviewLayout;

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

    public MainScreenFragment() {
    }

    public static MainScreenFragment newInstance() {
        MainScreenFragment f = new MainScreenFragment();
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);

        pagerAdapter = new MainPagerAdapter(getActivity(), getFragmentManager());

        viewPager = (ControlSwipeViewPager) rootView.findViewById(R.id.mainScreenPager);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(MainPagerAdapter.PICTURE_COMPARATOR_POSITION);
        viewPager.setPagingEnabled(true);
        viewPager.setOffscreenPageLimit(2);

        cameraPreviewLayout = (CameraPreviewLayout) rootView.findViewById(R.id.cameraPreviewLayout);

        leftButton = (Button) rootView.findViewById(R.id.leftButton);
        rightButton = (Button) rootView.findViewById(R.id.rightButton);

        upLeftButton = (Button) rootView.findViewById(R.id.upLeftButton);
        upButton = (Button) rootView.findViewById(R.id.upButton);
        upRightButton = (Button) rootView.findViewById(R.id.upRightButton);

        bottomLeftButton = (Button) rootView.findViewById(R.id.bottomLeftButton);
        bottomButton = (Button) rootView.findViewById(R.id.bottomButton);
        bottomRightButton = (Button) rootView.findViewById(R.id.bottomRightButton);

        cameraPreviewLayout.init();

        cameraPreviewLayout.setCallback(new CameraPreviewLayout.Callback() {
            @Override
            public void onCameraFailed(int error) {
            }

            @Override
            public void onPictureTaken(byte[] picturesBytes, Camera camera) {
            }
        });


        leftButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
//                                              pictureComparatorLayout.openChoice1Animation();
                                          }
                                      }

        );

        rightButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
//                                               pictureComparatorLayout.openChoice2Animation();
                                           }
                                       }

        );


        upLeftButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
//                                                glPictureChoice1.deleteTexture();
//                                                pictureComparatorLayout.updateLayout();
                                            }
                                        }

        );

        upButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
//                                            pictureComparatorLayout.swapeTextures();
                                        }
                                    }

        );

        upRightButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
//                                                 glPictureChoice2.deleteTexture();
//                                                 pictureComparatorLayout.updateLayout();
                                             }
                                         }

        );


        bottomLeftButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    if (bottomLeftButtonFlag) {
                                                        Utils.v(TAG, "drawable change");
//                                                        glPictureChoice1.setTextureChange(new TextureChange(getActivity(), R.drawable.choice1));
//                                                        glPictureChoice2.setTextureChange(new TextureChange(getActivity(), R.drawable.choice2));
                                                    } else {
                                                        Utils.v(TAG, "assets change");
//                                                        glPictureChoice1.setTextureChange(new TextureChange(getActivity(), "choice1-2.png"));
//                                                        glPictureChoice2.setTextureChange(new TextureChange(getActivity(), "choice2-2.png"));
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
//                                                glPictureChoice1.setTextureChange(new TextureChange(getActivity(), R.drawable.choice1));
//                                                glPictureChoice2.setTextureChange(new TextureChange(getActivity(), R.drawable.choice2));
                                            }
                                        }

        );

        bottomRightButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
//                                                     if (bottomRightButtonFlag)
//                                                         pictureComparatorLayout.setVisibility(View.VISIBLE);
//                                                     else
//                                                         pictureComparatorLayout.setVisibility(View.GONE);

                                                     bottomRightButtonFlag = !bottomRightButtonFlag;
                                                 }
                                             }

        );

        return rootView;

    }

    @Override
    public void onPause() {
        super.onPause();

        cameraPreviewLayout.stopPreview();
    }

    @Override
    public void onResume() {
        super.onResume();

        cameraPreviewLayout.startPreview(getActivity(), Camera.CameraInfo.CAMERA_FACING_BACK);
    }

}