package ledkis.module.picturecomparator.example.ui.view;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import ledkis.module.picturecomparator.example.R;


public class CameraPreviewLayout extends RelativeLayout {

    public static final int FAILED_TO_TAKE_THE_PICTURE = 60;
    public static final int FAILED_TO_START_THE_CAMERA = 61;

    public interface Callback extends Camera.PictureCallback {
        public void onCameraFailed(int error);
    }

    //  TODO moche ui
    CameraPreview mPreview;

    // TODO useless ? (extends Relative Layout)
    RelativeLayout cameraPreview;

    Callback callback;

    public CameraPreviewLayout(Context context) {
        super(context);
    }

    public CameraPreviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraPreviewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        inflate(getContext(), R.layout.camera_preview_layout, this);
        cameraPreview = (RelativeLayout) findViewById(R.id.cameraPreview);
    }


    public void takePicture() {
        if (null == mPreview) {
            if (null != callback)
                callback.onCameraFailed(FAILED_TO_TAKE_THE_PICTURE);
            return;
        }
        try {
            // TODO bug Caused by: java.lang.RuntimeException: takePicture failed
            mPreview.takePicture();
        } catch (Exception e) {
            if (null != callback)
                callback.onCameraFailed(FAILED_TO_TAKE_THE_PICTURE);

        }
    }

    public void stopPreview() {
        if (mPreview != null) {
            mPreview.stop();
            cameraPreview.removeView(mPreview);
        }
    }

    public void startPreview(Activity activity, int cameraId) {
        stopPreview();
        // createCameraPreview
        if (null != callback) {
            try {
                // TODO bug ui : CAMERA_FACING_FRONT is mirror like : everything is inverted
                mPreview = new CameraPreview(activity, callback, cameraId, CameraPreview.LayoutMode.FitToParent);
                LayoutParams previewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                cameraPreview.addView(mPreview, 0, previewLayoutParams);

            } catch (Exception e) {
                // TODO analytics
                if (null != callback)
                    callback.onCameraFailed(FAILED_TO_START_THE_CAMERA);

            }
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

}
