package ledkis.module.picturecomparator.example.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import ledkis.module.picturecomparator.GlPictureChoice;
import ledkis.module.picturecomparator.PictureComparatorRenderer;
import ledkis.module.picturecomparator.example.PictureComparatorApplication;
import ledkis.module.picturecomparator.example.R;
import ledkis.module.picturecomparator.example.core.AndroidBus;
import ledkis.module.picturecomparator.example.event.RequestSwitchVisibilityEvent;
import ledkis.module.picturecomparator.util.TextureChange;
import ledkis.module.picturecomparator.util.Utils;

public class PictureComparatorFragment extends Fragment {

    public static final String TAG = "PictureComparatorFragment";

    @Inject AndroidBus bus;

    private GlPictureChoice glPictureChoice1;
    private GlPictureChoice glPictureChoice2;

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

        leftButton = (Button) rootView.findViewById(R.id.leftButton);
        rightButton = (Button) rootView.findViewById(R.id.rightButton);

        upLeftButton = (Button) rootView.findViewById(R.id.upLeftButton);
        upButton = (Button) rootView.findViewById(R.id.upButton);
        upRightButton = (Button) rootView.findViewById(R.id.upRightButton);

        bottomLeftButton = (Button) rootView.findViewById(R.id.bottomLeftButton);
        bottomButton = (Button) rootView.findViewById(R.id.bottomButton);
        bottomRightButton = (Button) rootView.findViewById(R.id.bottomRightButton);

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
                if (bottomButtonFlag)
                    pictureComparatorLayout.setOnTouchListener(null);
                else
                    pictureComparatorLayout.initTouchControl();
            }
        });

        bottomRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new RequestSwitchVisibilityEvent(bottomRightButtonFlag));
                bottomRightButtonFlag = !bottomRightButtonFlag;

            }
        });

        return rootView;

    }

    @Subscribe
    public void onRequestSwitchVisibilityEvent(RequestSwitchVisibilityEvent event) {
        if (event.isVisibility())
            pictureComparatorLayout.setVisibility(View.VISIBLE);
        else
            pictureComparatorLayout.setVisibility(View.GONE);
    }
}
