package ledkis.module.picturecomparator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.view.animation.Interpolator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ledkis.module.picturecomparator.objects.Rect2DFrame;
import ledkis.module.picturecomparator.programs.ColorShaderProgram;
import ledkis.module.picturecomparator.programs.TextureShaderProgram;
import ledkis.module.picturecomparator.util.CubicBezierInterpolator;
import ledkis.module.picturecomparator.util.TextureChange;
import ledkis.module.picturecomparator.util.Utils;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static ledkis.module.picturecomparator.Constants.Layout.ANSWER_CHOICE_1;
import static ledkis.module.picturecomparator.Constants.Layout.CENTER_CHOICE_X;
import static ledkis.module.picturecomparator.Constants.Layout.CENTER_LINE_WIDTH;
import static ledkis.module.picturecomparator.Constants.Layout.CENTER_WIDTH;
import static ledkis.module.picturecomparator.Constants.Layout.CHOICE1_START_X;
import static ledkis.module.picturecomparator.Constants.Layout.CHOICE2_START_X;
import static ledkis.module.picturecomparator.Constants.Layout.CHOICE_1_FINAL_PROGRESS_VALUE;
import static ledkis.module.picturecomparator.Constants.Layout.CHOICE_2_FINAL_PROGRESS_VALUE;
import static ledkis.module.picturecomparator.Constants.Layout.CHOICE_THRESHOLD;
import static ledkis.module.picturecomparator.Constants.Layout.FADE_TIME;
import static ledkis.module.picturecomparator.Constants.Layout.MAX_ABS_PROGRESS_VALUE;
import static ledkis.module.picturecomparator.Constants.Layout.NO_CLIP;
import static ledkis.module.picturecomparator.Constants.Layout.PROGRESS_CENTER_VALUE;
import static ledkis.module.picturecomparator.Constants.Layout.X0;
import static ledkis.module.picturecomparator.Constants.Layout.X1;
import static ledkis.module.picturecomparator.Constants.Layout.Y0;
import static ledkis.module.picturecomparator.Constants.Layout.Y1;
import static ledkis.module.picturecomparator.Constants.MAX_NORMALIZED_DEVICE_X;
import static ledkis.module.picturecomparator.Constants.MIN_NORMALIZED_DEVICE_X;
import static ledkis.module.picturecomparator.Constants.NORMALIZED_DEVICE_MAX_HEIGHT;
import static ledkis.module.picturecomparator.Constants.NORMALIZED_DEVICE_MAX_WIDTH;

public class PictureComparatorRenderer implements Renderer {

    public static final String TAG = "PictureComparatorRenderer";

    public interface Callback {

        void onHandleTouchPress(float normalizedX, float normalizedY);

        void onHandleTouchDrag(float normalizedX, float normalizedY);

        void onHandleTouchUp(float normalizedX, float normalizedY);

    }

    public interface OnSurfaceCreatedCallback {
        void onSurfaceCreated();
    }

    public interface OnProgressChangeCallback {
        void onProgressChange(float progress);
    }

    public interface OnPicturesStateChangeCallback {
        void onPicturesStateChange(PicturesState picturesState);
    }

    public interface OnDisplayStateChangeCallback {
        void onDisplayStateChange(DisplayState displayState);
    }

    public enum PicturesState {
        CHOICE_1,
        CHOICE_2,
        CHOICE_1_AND_2,
        NONE
    }

    public enum DisplayState {
        CHOICE_1,
        CHOICE_2,
        CENTER,
    }

    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] modelProjectionMatrix = new float[16];

    private Rect2DFrame centerLine;

    private GlPictureChoice glPictureChoice1;
    private GlPictureChoice glPictureChoice2;

    private TextureShaderProgram textureChoice1Program;
    private TextureShaderProgram textureChoice2Program;

    private ColorShaderProgram colorShaderProgram;

    private float currentProgress;
    private float lastNormalizedX;

    private float layoutRatio;

    private float x1, x2,     // position
            w1, w2,   // width
            wf1, wf2, // width factor
            cw1, cw2, // clip width factor
            ch1, ch2; // clip height factor

    private float centerX;

    private int centerLineColor;

    private Callback callback;
    private OnSurfaceCreatedCallback onSurfaceCreatedCallback;
    private OnProgressChangeCallback onProgressChangeCallback;
    private OnPicturesStateChangeCallback onPicturesStateChangeCallback;
    private OnDisplayStateChangeCallback onDisplayStateChangeCallback;

    private GLSurfaceView glSurfaceView;

    private boolean animate;
    private boolean onAnimation;
    private long animationStartTime;
    private float finalValue;
    private float releaseProgress;

    private Interpolator interpolator;

    private PicturesState lastPicturesState;
    private PicturesState picturesState;

    private DisplayState displayState;

    public PictureComparatorRenderer(Context context, GLSurfaceView glSurfaceView) {
        this.context = context;
        this.glSurfaceView = glSurfaceView;

        this.animate = true;

        interpolator = new CubicBezierInterpolator(X0, Y0, X1, Y1);
        centerLineColor = Color.WHITE;

        displayState = DisplayState.CENTER;

//        layoutRatio = 1.7777778f;

    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        lastNormalizedX = normalizedX;

        onAnimation = false;

        if (null != callback)
            callback.onHandleTouchPress(normalizedX, normalizedY);
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {

        float progress = Utils.clipProgress(currentProgress + (normalizedX - lastNormalizedX));
        lastNormalizedX = normalizedX;

        onAnimation = false;

        setLayout(progress);

        if (null != callback)
            callback.onHandleTouchDrag(normalizedX, normalizedY);
    }

    public void handleTouchUp(float normalizedX, float normalizedY) {

        float progress = Utils.clipProgress(currentProgress + (normalizedX - lastNormalizedX));
        lastNormalizedX = normalizedX;

        releaseAnimation(progress);

        setLayout(progress);

        if (null != callback)
            callback.onHandleTouchUp(normalizedX, normalizedY);

    }

    private void setCurrentProgress(float progress) {
        currentProgress = progress;
        if (null != onProgressChangeCallback)
            onProgressChangeCallback.onProgressChange(currentProgress);
    }

    public void updateState() {

        lastPicturesState = picturesState;

        boolean pic1 = isPicture1Ready();
        boolean pic2 = isPicture2Ready();

        if (pic1 && !pic2) {
            picturesState = PicturesState.CHOICE_1;
        } else if (!pic1 && pic2) {
            picturesState = PicturesState.CHOICE_2;
        } else if (pic1 && pic2) {
            picturesState = PicturesState.CHOICE_1_AND_2;
        } else {
            picturesState = PicturesState.NONE;
        }

        if (lastPicturesState != picturesState) {
            if (null != onPicturesStateChangeCallback) {
                onPicturesStateChangeCallback.onPicturesStateChange(picturesState);
            }
            Utils.v(TAG, "onPicturesStateChange:" + picturesState);
        }
    }

    public void updateLayout() {
        setLayout(currentProgress);
    }

    public void setLayout(float progress) {

        updateState();

        if (picturesState == PicturesState.CHOICE_1_AND_2) {
            if (null == centerLine)
                setCenterLine();
        } else {
            centerLine = null;
        }

        switch (picturesState) {
            case CHOICE_1:
                progress = CHOICE_1_FINAL_PROGRESS_VALUE;
                break;
            case CHOICE_2:
                progress = CHOICE_2_FINAL_PROGRESS_VALUE;
                break;
            case NONE:
                progress = PROGRESS_CENTER_VALUE;
                break;
        }

        boolean from1to2Pictures = (PicturesState.CHOICE_1 == lastPicturesState || PicturesState.CHOICE_2 == lastPicturesState)
                && (PicturesState.CHOICE_1_AND_2 == picturesState);

        if (from1to2Pictures)
            progress = PROGRESS_CENTER_VALUE;

        setCurrentProgress(progress);

        evalPicPosition(progress);
        evalPicWidth(progress);
        evalPicClipping(w1, w2);
    }

    private void evalPicPosition(float progress) {

        centerX = progress;

        if (ANSWER_CHOICE_1 == Utils.getAnswerChoice(progress)) {
            x1 = Utils.map(Math.abs(progress), PROGRESS_CENTER_VALUE,
                    MAX_ABS_PROGRESS_VALUE, CHOICE1_START_X, CENTER_CHOICE_X);
            x2 = Utils.map(Math.abs(progress), PROGRESS_CENTER_VALUE,
                    MAX_ABS_PROGRESS_VALUE, CHOICE2_START_X, MAX_NORMALIZED_DEVICE_X);

        } else {
            x1 = Utils.map(Math.abs(progress), PROGRESS_CENTER_VALUE,
                    MAX_ABS_PROGRESS_VALUE, CHOICE1_START_X, MIN_NORMALIZED_DEVICE_X);
            x2 = Utils.map(Math.abs(progress), PROGRESS_CENTER_VALUE,
                    MAX_ABS_PROGRESS_VALUE, CHOICE2_START_X, CENTER_CHOICE_X);
        }
    }

    private void evalPicWidth(float progress) {
        if (ANSWER_CHOICE_1 == Utils.getAnswerChoice(progress)) {
            w1 = Utils.map(Math.abs(progress), PROGRESS_CENTER_VALUE,
                    MAX_ABS_PROGRESS_VALUE, CENTER_WIDTH, NORMALIZED_DEVICE_MAX_WIDTH);
            w2 = Utils.map(Math.abs(progress), PROGRESS_CENTER_VALUE,
                    MAX_ABS_PROGRESS_VALUE, CENTER_WIDTH, 0f);

        } else {
            w1 = Utils.map(Math.abs(progress), PROGRESS_CENTER_VALUE,
                    MAX_ABS_PROGRESS_VALUE, CENTER_WIDTH, 0f);
            w2 = Utils.map(Math.abs(progress), PROGRESS_CENTER_VALUE,
                    MAX_ABS_PROGRESS_VALUE, CENTER_WIDTH, NORMALIZED_DEVICE_MAX_WIDTH);

        }

        // TODO ui clipping : interresting result with getHeight
        if (isPicture1Ready())
            wf1 = w1 / glPictureChoice1.getWidth();
        if (isPicture2Ready())
            wf2 = w2 / glPictureChoice2.getWidth();
    }

    private void evalPicClipping(float pic1Width, float pic2Width) {

        // Pic1
        if (isPicture1Ready()) {
            float texture1Ratio = glPictureChoice1.getTextureRatio();
            float choice1Ratio = (NORMALIZED_DEVICE_MAX_WIDTH * NORMALIZED_DEVICE_MAX_HEIGHT) / (pic1Width);

            cw1 = NO_CLIP;
            ch1 = (0.5f * texture1Ratio * pic1Width) / NORMALIZED_DEVICE_MAX_HEIGHT;

            if (texture1Ratio > choice1Ratio) {
                cw1 = NO_CLIP;
                ch1 = NORMALIZED_DEVICE_MAX_HEIGHT / (0.5f * texture1Ratio * pic1Width);
            } else {
                cw1 = (0.5f * texture1Ratio * pic1Width) / (NORMALIZED_DEVICE_MAX_WIDTH);
                ch1 = NO_CLIP;
            }
        }

        // Pic2
        if (isPicture2Ready()) {
            float texture2Ratio = glPictureChoice2.getTextureRatio();
            float choice2Ratio = (NORMALIZED_DEVICE_MAX_WIDTH * NORMALIZED_DEVICE_MAX_HEIGHT) / (pic2Width);

            cw2 = NO_CLIP;
            ch2 = (0.5f * texture2Ratio * pic2Width) / NORMALIZED_DEVICE_MAX_HEIGHT;

            if (texture2Ratio > choice2Ratio) {
                cw2 = NO_CLIP;
                ch2 = NORMALIZED_DEVICE_MAX_HEIGHT / (0.5f * texture2Ratio * pic2Width);
            } else {
                cw2 = (0.5f * texture2Ratio * pic2Width) / (NORMALIZED_DEVICE_MAX_WIDTH);
                ch2 = NO_CLIP;
            }
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setOnSurfaceCreatedCallback(OnSurfaceCreatedCallback onSurfaceCreatedCallback) {
        this.onSurfaceCreatedCallback = onSurfaceCreatedCallback;
    }

    public void setOnProgressChangeCallback(OnProgressChangeCallback onProgressChangeCallback) {
        this.onProgressChangeCallback = onProgressChangeCallback;
    }

    public void setOnPicturesStateChangeCallback(OnPicturesStateChangeCallback onPicturesStateChangeCallback) {
        this.onPicturesStateChangeCallback = onPicturesStateChangeCallback;
    }

    public void setOnDisplayStateChangeCallback(OnDisplayStateChangeCallback onDisplayStateChangeCallback) {
        this.onDisplayStateChangeCallback = onDisplayStateChangeCallback;
    }

    public void setGlPictureChoice1(GlPictureChoice glPictureChoice1) {
        this.glPictureChoice1 = glPictureChoice1;
    }

    public void setGlPictureChoice2(GlPictureChoice glPictureChoice2) {
        this.glPictureChoice2 = glPictureChoice2;
    }

    public float getCurrentProgress() {
        return currentProgress;
    }

    public PicturesState getPicturesState() {
        return picturesState;
    }

    public DisplayState getDisplayState() {
        return displayState;
    }

    public void swapeTextures(){
        // TODO moche
        if (isPicture1Ready() && isPicture2Ready()) {
            Bitmap texture1Bitmap = glPictureChoice1.getTextureBitmap();
            TextureChange texture2Change = new TextureChange(texture1Bitmap.copy(texture1Bitmap.getConfig(), true));

            Bitmap texture2Bitmap = glPictureChoice2.getTextureBitmap();
            TextureChange texture1Change = new TextureChange(texture2Bitmap.copy(texture2Bitmap.getConfig(), true));

            glPictureChoice1.setTextureChange(texture1Change);
            glPictureChoice2.setTextureChange(texture2Change);
        }
    }

    public void setCenterLine() {
        centerLine = new Rect2DFrame(CENTER_LINE_WIDTH, NORMALIZED_DEVICE_MAX_HEIGHT);
    }

    private void positionAndScaleObject2DInScene(float x, float y, float scaleXFactor, float scaleYFactor) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, 0f);
        scaleM(modelMatrix, 0, scaleXFactor, scaleYFactor, 1f);
        multiplyMM(modelProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        layoutRatio = (float) glSurfaceView.getHeight() / (float) glSurfaceView.getWidth();

        setLayout(PROGRESS_CENTER_VALUE);

        textureChoice1Program = new TextureShaderProgram(context);
        textureChoice2Program = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);

        if (null != onSurfaceCreatedCallback)
            onSurfaceCreatedCallback.onSurfaceCreated();

        Utils.v(TAG, "onSurfaceCreated, layoutRatio: " + layoutRatio);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        orthoM(projectionMatrix, 0, -1f, 1f, -1f, 1f, -1f, 1f);

        Utils.v(TAG, "onSurfaceChanged w:" + width + " h:" + height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        updatePicturesInitialization();

        // ReleaseAnimation & change texture
        if (animate && onAnimation) {
            onAnimation();
        } else {
            updateTextures();
        }

        // Picture 1
        if (isPicture1Ready() && null != textureChoice1Program) {

            glPictureChoice1.clipTexture(cw1, ch1);
            positionAndScaleObject2DInScene(x1, 0f, wf1, 1f);
            textureChoice1Program.useProgram();
            textureChoice1Program.setUniforms(modelProjectionMatrix, glPictureChoice1.getTextureId());
            glPictureChoice1.bindData(textureChoice1Program);
            glPictureChoice1.draw();
        }

        // Picture 2
        if (isPicture2Ready() && null != textureChoice2Program) {
            glPictureChoice2.clipTexture(cw2, ch2);
            positionAndScaleObject2DInScene(x2, 0f, wf2, 1f);
            textureChoice2Program.useProgram();
            textureChoice2Program.setUniforms(modelProjectionMatrix, glPictureChoice2.getTextureId());
            glPictureChoice2.bindData(textureChoice2Program);
            glPictureChoice2.draw();
        }

        // CenterLine
        if (null != centerLine && null != colorShaderProgram) {
            positionAndScaleObject2DInScene(centerX, 0f, 1f, 1f);
            colorShaderProgram.useProgram();
            colorShaderProgram.setUniforms(modelProjectionMatrix,
                    (float) Color.red(centerLineColor) / 255,
                    (float) Color.green(centerLineColor) / 255,
                    (float) Color.blue(centerLineColor) / 255);
            centerLine.bindData(colorShaderProgram);
            centerLine.draw();
        }
    }

    private void onAnimation() {
        long t = System.currentTimeMillis() - animationStartTime;

        float nt = interpolator.getInterpolation(Utils.map(t, 0f, FADE_TIME, 0f, 1f));

        float progress = Utils.map(nt, 0f, 1f, releaseProgress, finalValue);

        setLayout(progress);

        if (t > FADE_TIME) {
            onAnimation = false;
        }
    }

    private void updateTextures() {

        boolean texture1Changed = null != glPictureChoice1 && glPictureChoice1.updateBitmap(context, layoutRatio);
        boolean texture2Changed = null != glPictureChoice2 && glPictureChoice2.updateBitmap(context, layoutRatio);

        if (texture1Changed || texture2Changed)
            updateLayout();
    }

    private void releaseAnimation(float progress) {
        finalValue = Utils.getFinalThresholdValue(progress, CHOICE_THRESHOLD);
        if (CHOICE_1_FINAL_PROGRESS_VALUE == finalValue) {
            openChoice1Animation();
        } else if (CHOICE_2_FINAL_PROGRESS_VALUE == finalValue) {
            openChoice2Animation();
        } else {
            closeAnimation();
        }
    }

    public void openChoice1Animation() {
        onAnimation = true;
        animationStartTime = System.currentTimeMillis();
        releaseProgress = currentProgress;
        finalValue = CHOICE_1_FINAL_PROGRESS_VALUE;
        displayState = DisplayState.CHOICE_1;
        if (null != onDisplayStateChangeCallback) {
            onDisplayStateChangeCallback.onDisplayStateChange(DisplayState.CHOICE_1);
        }
    }

    public void openChoice2Animation() {
        onAnimation = true;
        animationStartTime = System.currentTimeMillis();
        releaseProgress = currentProgress;
        finalValue = CHOICE_2_FINAL_PROGRESS_VALUE;
        displayState = DisplayState.CHOICE_2;
        if (null != onDisplayStateChangeCallback) {
            onDisplayStateChangeCallback.onDisplayStateChange(DisplayState.CHOICE_2);
        }
    }

    public void closeAnimation() {
        onAnimation = true;
        animationStartTime = System.currentTimeMillis();
        releaseProgress = currentProgress;
        finalValue = PROGRESS_CENTER_VALUE;
        displayState = DisplayState.CENTER;
        if (null != onDisplayStateChangeCallback) {
            onDisplayStateChangeCallback.onDisplayStateChange(DisplayState.CENTER);
        }
    }

    private void updatePicturesInitialization() {
        if (null != glPictureChoice1) {
            glPictureChoice1.initTexture();
        }

        if (null != glPictureChoice2) {
            glPictureChoice2.initTexture();
        }
    }

    private boolean isPicture1Ready() {
        return null != glPictureChoice1 && glPictureChoice1.isFrameInitialized();
    }

    private boolean isPicture2Ready() {
        return null != glPictureChoice2 && glPictureChoice2.isFrameInitialized();
    }

}