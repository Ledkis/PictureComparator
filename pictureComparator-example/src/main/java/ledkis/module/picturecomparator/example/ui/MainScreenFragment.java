package ledkis.module.picturecomparator.example.ui;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import ledkis.module.picturecomparator.Constants;
import ledkis.module.picturecomparator.example.PictureComparatorApplication;
import ledkis.module.picturecomparator.example.R;
import ledkis.module.picturecomparator.example.core.AndroidBus;
import ledkis.module.picturecomparator.example.event.PictureTakenEvent;
import ledkis.module.picturecomparator.example.event.RequestSwitchCameraEvent;
import ledkis.module.picturecomparator.example.event.RequestSwitchVisibilityEvent;
import ledkis.module.picturecomparator.example.event.RequestTakePictureEvent;
import ledkis.module.picturecomparator.example.ui.view.CameraPreviewLayout;
import ledkis.module.picturecomparator.util.Utils;

public class MainScreenFragment extends Fragment {

    public static final String TAG = "MainScreenFragment";

    @Inject AndroidBus bus;

    private MainPagerAdapter pagerAdapter;
    private ControlSwipeViewPager viewPager;

    private CameraPreviewLayout cameraPreviewLayout;

    private int pictureClass;

    private int cameraId;

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
        cameraPreviewLayout.startPreview(getActivity(), cameraId);
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

        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);

        viewPager = (ControlSwipeViewPager) rootView.findViewById(R.id.mainScreenPager);

        pagerAdapter = new MainPagerAdapter(getActivity(), getFragmentManager());
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
                Bitmap bitmap = rotateBitmap(
                        BitmapFactory.decodeByteArray(picturesBytes, 0, picturesBytes.length),
                        cameraId);

                bus.post(new PictureTakenEvent(bitmap, pictureClass));
                bus.post(new RequestSwitchVisibilityEvent());
            }
        });

        return rootView;
    }

    public Bitmap rotateBitmap(Bitmap bitmap, int cameraState) {
        if (Camera.CameraInfo.CAMERA_FACING_BACK == cameraState) {
            return Utils.rotateBitmap(bitmap, Constants.Layout.FRONT_BITMAP_PRE_ROTATION);
        } else if (Camera.CameraInfo.CAMERA_FACING_FRONT == cameraState) {
            return Utils.rotateBitmap(bitmap, Constants.Layout.BACK_BITMAP_PRE_ROTATION);
        } else {
            return Utils.rotateBitmap(bitmap, Constants.Layout.CUSTOM_BITMAP_PRE_ROTATION);
        }
    }

    @Subscribe
    public void onRequestSwitchVisibilityEvent(RequestSwitchVisibilityEvent event) {
        if (event.getVisibilityFlag()) {
            cameraPreviewLayout.stopPreview();
            cameraPreviewLayout.setVisibility(View.GONE);
            viewPager.setPagingEnabled(false);
        } else {
            cameraPreviewLayout.startPreview(getActivity(), cameraId);
            cameraPreviewLayout.setVisibility(View.VISIBLE);
            viewPager.setPagingEnabled(true);
        }
    }

    @Subscribe
    public void onPictureTakenEvent(PictureTakenEvent event) {
        cameraPreviewLayout.startPreview(getActivity(), cameraId);
        cameraPreviewLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onRequestTakePictureEvent(RequestTakePictureEvent event) {
        cameraPreviewLayout.takePicture();
        pictureClass = event.getPictureClass();
    }

    @Subscribe
    public void onRequestSwitchCameraEvent(RequestSwitchCameraEvent event) {
        cameraId = event.getCameraId();
        cameraPreviewLayout.startPreview(getActivity(), cameraId);
    }



}