package ledkis.module.picturecomparator;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ledkis.module.picturecomparator.util.Utils;

public class GLPictureComparatorLayout extends RelativeLayout {

    public static final String TAG = "GLPictureComparatorLayout";

    /**
     * Hold a reference to our GLSurfaceView
     */
    private GLSurfaceView glSurfaceView;
    private PictureComparatorRenderer render;
    private boolean rendererSet = false;


    private PictureComparatorRenderer.OnSurfaceCreatedCallback onSurfaceCreatedCallback;

    public GLPictureComparatorLayout(Context context) {
        super(context);
        initView();
    }

    public GLPictureComparatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GLPictureComparatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        inflate(getContext(), R.layout.picture_comparator_layout, this);

        glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface_view);

        if (supportsEs2()) {
            // ...
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);

            //  http://stackoverflow.com/questions/14167319/android-opengl-demo-no-config-chosen
            glSurfaceView.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);

            render = new PictureComparatorRenderer(getContext(), glSurfaceView);

            // Assign our renderer.
            glSurfaceView.setRenderer(render);
            rendererSet = true;

            Utils.v(TAG, "rendererSet");
        } else {
            /*
             * This is where you could create an OpenGL ES 1.x compatible
             * renderer if you wanted to support both ES 1 and ES 2. Since
             * we're not doing anything, the app will crash if the device
             * doesn't support OpenGL ES 2.0. If we publish on the market, we
             * should also add the following to AndroidManifest.xml:
             *
             * <uses-feature android:glEsVersion="0x00020000"
             * android:required="true" />
             *
             * This hides our app from those devices which don't support OpenGL
             * ES 2.0.
             */
            Toast.makeText(getContext(), "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show();
            return;
        }

        glSurfaceView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event != null) {
                            // Convert touch coordinates into normalized device
                            // coordinates, keeping in mind that Android's Y
                            // coordinates are inverted.
                            final float normalizedX =
                                    (event.getX() / (float) v.getWidth()) * 2 - 1;
                            final float normalizedY =
                                    -((event.getY() / (float) v.getHeight()) * 2 - 1);

                            //  Since  Android’s  GLSurfaceView  does  rendering  in  a  background  thread,  we  must  be
                            //  careful to call OpenGL only within the rendering thread, and Android UI calls only
                            //  within Android’s main thread. We can call queueEvent() on our instance of GLSurfaceView
                            //  to post a Runnable on the background rendering thread. From within the rendering
                            //  thread, we can call runOnUIThread() on our activity to post events on the main thread.

                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                glSurfaceView.queueEvent(new Runnable() {
                                    @Override
                                    public void run() {
                                        render.handleTouchPress(
                                                normalizedX, normalizedY);
                                    }
                                });
                            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                glSurfaceView.queueEvent(new Runnable() {
                                    @Override
                                    public void run() {
                                        render.handleTouchDrag(
                                                normalizedX, normalizedY);
                                    }
                                });
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                glSurfaceView.queueEvent(new Runnable() {
                                    @Override
                                    public void run() {
                                        render.handleTouchUp(
                                                normalizedX, normalizedY);
                                    }
                                });
                            }

                            return true;
                        } else {
                            return false;
                        }
                    }
                });

        render.setOnSurfaceCreatedCallback(new PictureComparatorRenderer.OnSurfaceCreatedCallback() {
            @Override
            public void onSurfaceCreated() {
                if (null != onSurfaceCreatedCallback)
                    onSurfaceCreatedCallback.onSurfaceCreated();
            }
        });
    }

    public void setOnSurfaceCreatedCallback(PictureComparatorRenderer.OnSurfaceCreatedCallback onSurfaceCreatedCallback) {
        this.onSurfaceCreatedCallback = onSurfaceCreatedCallback;
    }

    public boolean supportsEs2() {
        // Check if the system supports OpenGL ES 2.0.
        ActivityManager activityManager =
                (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();
        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.

        return configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")));
    }

    public void swapeTextures(){
        render.swapeTextures();
    }

    public void pause() {
        if (rendererSet) {
            glSurfaceView.onPause();
            Utils.v(TAG, "pause");
        }
    }

    public void resume() {
        if (rendererSet) {
            glSurfaceView.onResume();
            Utils.v(TAG, "resume");
        }
    }

    public GLSurfaceView getGlSurfaceView() {
        return glSurfaceView;
    }

    public boolean isRendererSet() {
        return rendererSet;
    }

    public void openChoice1Animation() {
        if (null != render)
            render.openChoice1Animation();
    }

    public void openChoice2Animation() {
        if (null != render)
            render.openChoice2Animation();
    }

    public void closeAnimation() {
        if (null != render)
            render.closeAnimation();
    }

    public void updateLayout() {
        render.updateLayout();
    }

    public void setGlPictureChoices(GlPictureChoice glPictureChoice1, GlPictureChoice glPictureChoice2) {
        render.setGlPictureChoice1(glPictureChoice1);
        render.setGlPictureChoice2(glPictureChoice2);
    }

}
