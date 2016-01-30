package ledkis.module.picturecomparator.example.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import ledkis.module.picturecomparator.Constants;
import ledkis.module.picturecomparator.GlPictureChoice;
import ledkis.module.picturecomparator.PictureComparatorRenderer;
import ledkis.module.picturecomparator.example.PictureComparatorApplication;
import ledkis.module.picturecomparator.example.R;
import ledkis.module.picturecomparator.example.core.AndroidBus;
import ledkis.module.picturecomparator.example.event.PictureTakenEvent;
import ledkis.module.picturecomparator.example.event.RequestSwitchCameraEvent;
import ledkis.module.picturecomparator.example.event.RequestSwitchVisibilityEvent;
import ledkis.module.picturecomparator.example.event.RequestTakePictureEvent;
import ledkis.module.picturecomparator.example.ui.view.PictureComparatorLayout;
import ledkis.module.picturecomparator.util.TextureChange;
import ledkis.module.picturecomparator.util.Utils;

public class PictureComparatorFragment extends Fragment {

    public static final String TAG = "PictureComparatorFragment";

    @Inject AndroidBus bus;

    private GlPictureChoice glPictureChoice1;
    private GlPictureChoice glPictureChoice2;

    private PictureComparatorLayout pictureComparatorLayout;

    private RelativeLayout pictureComparatorControlLayout;
    private RelativeLayout cameraControlLayout;

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

    private Button takeChoice1Picture;
    private Button takeChoice2Picture;
    private Button switchCameraButton;

    private Button visibilityButton;


    public PictureComparatorFragment() {
    }

    public static PictureComparatorFragment newInstance() {
        PictureComparatorFragment f = new PictureComparatorFragment();
        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        pictureComparatorLayout.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
        pictureComparatorLayout.pause();
        pictureComparatorLayout.setZOrderMediaOverlay(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PictureComparatorApplication.get(getActivity()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_picture_comparator, container, false);

        pictureComparatorLayout = (PictureComparatorLayout) rootView.findViewById(R.id.gl_picture_comparator_layout);

        pictureComparatorControlLayout = (RelativeLayout) rootView.findViewById(R.id.pictureComparatorControlLayout);
        cameraControlLayout = (RelativeLayout) rootView.findViewById(R.id.cameraControlLayout);

        leftButton = (Button) rootView.findViewById(R.id.leftButton);
        rightButton = (Button) rootView.findViewById(R.id.rightButton);

        upLeftButton = (Button) rootView.findViewById(R.id.upLeftButton);
        upButton = (Button) rootView.findViewById(R.id.upButton);
        upRightButton = (Button) rootView.findViewById(R.id.upRightButton);

        bottomLeftButton = (Button) rootView.findViewById(R.id.bottomLeftButton);
        bottomButton = (Button) rootView.findViewById(R.id.bottomButton);
        bottomRightButton = (Button) rootView.findViewById(R.id.bottomRightButton);

        takeChoice1Picture = (Button) rootView.findViewById(R.id.takeChoice1Picture);
        takeChoice2Picture = (Button) rootView.findViewById(R.id.takeChoice2Picture);
        switchCameraButton = (Button) rootView.findViewById(R.id.switchCameraButton);

        visibilityButton = (Button) rootView.findViewById(R.id.visibilityButton);

        glPictureChoice1 = new GlPictureChoice();
        glPictureChoice2 = new GlPictureChoice();

        pictureComparatorLayout.setGlPictureChoices(glPictureChoice1, glPictureChoice2);

        pictureComparatorLayout.setOnSurfaceCreatedCallback(new PictureComparatorRenderer.OnSurfaceCreatedCallback() {
            @Override
            public void onSurfaceCreated() {
                glPictureChoice1.setTextureChange(new TextureChange(getActivity(), R.drawable.choice1));
                glPictureChoice2.setTextureChange(new TextureChange(getActivity(), R.drawable.choice2));

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
                    glPictureChoice1.setTextureChange(new TextureChange(getActivity(), R.drawable.choice1));
                    glPictureChoice2.setTextureChange(new TextureChange(getActivity(), R.drawable.choice2));
                } else {
                    Utils.v(TAG, "assets change");
                    glPictureChoice1.setTextureChange(new TextureChange(getActivity(), "choice1-2.png"));
                    glPictureChoice2.setTextureChange(new TextureChange(getActivity(), "choice2-2.png"));
                }

                bottomLeftButtonFlag = !bottomLeftButtonFlag;
            }
        });

        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomButtonFlag = !bottomButtonFlag;

                if (bottomButtonFlag) {
                    pictureComparatorLayout.setOnTouchListener(null);
                } else {
                    pictureComparatorLayout.initTouchControl();
                }
            }
        });

        bottomRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                bus.post(new RequestSwitchVisibilityEvent(bottomRightButtonFlag));
//                bottomRightButtonFlag = !bottomRightButtonFlag;

            }
        });

        takeChoice1Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new RequestTakePictureEvent(Constants.Layout.PICTURE_CLASS_1));
            }
        });

        takeChoice2Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new RequestTakePictureEvent(Constants.Layout.PICTURE_CLASS_2));
            }
        });

        switchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new RequestSwitchCameraEvent());
            }
        });


        visibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new RequestSwitchVisibilityEvent());
            }
        });

        unDisplay();

        return rootView;

    }

    private void display() {
        pictureComparatorLayout.setVisibility(View.VISIBLE);
        pictureComparatorControlLayout.setVisibility(View.VISIBLE);
        pictureComparatorLayout.initTouchControl();
        cameraControlLayout.setVisibility(View.GONE);
    }

    private void unDisplay() {
        pictureComparatorLayout.setVisibility(View.GONE);
        pictureComparatorControlLayout.setVisibility(View.GONE);
        pictureComparatorLayout.setOnTouchListener(null);
        cameraControlLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onRequestSwitchVisibilityEvent(RequestSwitchVisibilityEvent event) {
        if (event.getVisibilityFlag()) {
            display();
        } else {
            unDisplay();
        }
    }

    @Subscribe
    public void onPictureTakenEvent(PictureTakenEvent event) {
        if (Constants.Layout.PICTURE_CLASS_1 == event.getPictureClass()) {
            glPictureChoice1.setTextureChange(new TextureChange(event.getBitmap()));
        } else {
            glPictureChoice2.setTextureChange(new TextureChange(event.getBitmap()));
        }
    }
}
