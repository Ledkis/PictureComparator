package ledkis.module.picturecomparator.example.ui;

import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import ledkis.module.picturecomparator.example.PictureComparatorApplication;
import ledkis.module.picturecomparator.example.R;
import ledkis.module.picturecomparator.example.core.AndroidBus;
import ledkis.module.picturecomparator.example.event.RequestSwitchVisibilityEvent;

public class MainScreenFragment extends Fragment {

    public static final String TAG = "MainScreenFragment";

    @Inject AndroidBus bus;

    private MainPagerAdapter pagerAdapter;
    private ControlSwipeViewPager viewPager;

    private CameraPreviewLayout cameraPreviewLayout;

    public MainScreenFragment() {
    }

    public static MainScreenFragment newInstance() {
        MainScreenFragment f = new MainScreenFragment();
        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        cameraPreviewLayout.startPreview(getActivity(), Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
        cameraPreviewLayout.stopPreview();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PictureComparatorApplication.get(getActivity()).inject(this);
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

        cameraPreviewLayout.init();

        cameraPreviewLayout.setCallback(new CameraPreviewLayout.Callback() {
            @Override
            public void onCameraFailed(int error) {
            }

            @Override
            public void onPictureTaken(byte[] picturesBytes, Camera camera) {
            }
        });

        return rootView;
    }

    @Subscribe
    public void onRequestSwitchVisibilityEvent(RequestSwitchVisibilityEvent event) {
        if (event.isVisibility()) {
            cameraPreviewLayout.stopPreview();
            cameraPreviewLayout.setVisibility(View.GONE);
        } else {
            cameraPreviewLayout.startPreview(getActivity(), Camera.CameraInfo.CAMERA_FACING_BACK);
            cameraPreviewLayout.setVisibility(View.VISIBLE);
        }
    }

}