package ledkis.module.picturecomparator.example;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ledkis.module.picturecomparator.GlPictureChoice;
import ledkis.module.picturecomparator.PictureComparatorRenderer;
import ledkis.module.picturecomparator.util.TextureChange;

public class PictureComparatorFragment extends Fragment {

    public static final String TAG = "PictureComparatorFragment";

    private GlPictureChoice glPictureChoice1;
    private GlPictureChoice glPictureChoice2;

    private PictureComparatorLayout pictureComparatorLayout;

    public PictureComparatorFragment() {
    }

    public static PictureComparatorFragment newInstance() {
        PictureComparatorFragment f = new PictureComparatorFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_picture_comparator, container, false);

        pictureComparatorLayout = (PictureComparatorLayout) rootView.findViewById(R.id.gl_picture_comparator_layout);

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

        return rootView;

    }

    @Override
    public void onPause() {
        super.onPause();
        pictureComparatorLayout.pause();
        pictureComparatorLayout.setZOrderMediaOverlay(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        pictureComparatorLayout.resume();
    }
}
