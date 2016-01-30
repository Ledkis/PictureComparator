package ledkis.module.picturecomparator.example.event;

import android.hardware.Camera;

public class RequestSwitchCameraEvent {

    private int cameraId;
    private static boolean cameraSwitchFlag;

    public RequestSwitchCameraEvent() {
        cameraSwitchFlag = !cameraSwitchFlag;
        if (cameraSwitchFlag) {
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    public int getCameraId() {
        return cameraId;
    }
}
