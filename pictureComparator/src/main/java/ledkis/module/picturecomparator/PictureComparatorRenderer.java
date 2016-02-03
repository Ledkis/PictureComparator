package ledkis.module.picturecomparator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.view.View;
import android.view.animation.Interpolator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ledkis.module.picturecomparator.objects.Rect2DFrame;
import ledkis.module.picturecomparator.programs.ColorShaderProgram;
import ledkis.module.picturecomparator.programs.TextureShaderProgram;
import ledkis.module.picturecomparator.util.CubicBezierInterpolator;
import ledkis.module.picturecomparator.util.Geometry2D;
import ledkis.module.picturecomparator.util.TextureChange;
import ledkis.module.picturecomparator.util.Utils;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
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
import static ledkis.module.picturecomparator.Constants.Layout.PICTURES_INVISIBLE;
import static ledkis.module.picturecomparator.Constants.Layout.PICTURES_VISIBLE;
import static ledkis.module.picturecomparator.Constants.Layout.PROGRESS_CENTER_VALUE;
import static ledkis.module.picturecomparator.Constants.Layout.PROGRESS_RECT_HEIGHT;
import static ledkis.module.picturecomparator.Constants.Layout.PROGRESS_RECT_HEIGHT_CENTER_FACTOR;
import static ledkis.module.picturecomparator.Constants.Layout.PROGRESS_RECT_WIDTH;
import static ledkis.module.picturecomparator.Constants.Layout.PROGRESS_RECT_WIDTH_MIN_FACTOR;
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

    public interface OnTouchCallback {

        void onTouchPress(float normalizedX, float normalizedY);

        void onTouchDrag(float normalizedX, float normalizedY);

        void onTouchUp(float normalizedX, float normalizedY);

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

    public interface OnProgressRectClickCallback {
        void onProgressRect1Click();

        void onProgressRect2Click();
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

    private Rect2DFrame choiceMaskFrame;
    private Rect2DFrame centerLine;

    private Rect2DFrame choice1ProgressRect;
    private Rect2DFrame choice2ProgressRect;

    private GlPictureChoice glPictureChoice1;
    private GlPictureChoice glPictureChoice2;

    private TextureShaderProgram textureChoice1Program;
    private TextureShaderProgram textureChoice2Program;

    private ColorShaderProgram colorShaderProgram;

    private float currentProgress;
    private float touchProgress;
    private float lastNormalizedX;

    private float layoutRatio;

    private float x1, x2,     // position
            w1, w2,   // width
            wf1, wf2, // width factor
            cw1, cw2, // clip width factor
            ch1, ch2; // clip height factor

    private float centerX;

    private float picturesVisibility;
    private float picturesAlpha;

    private int centerLineColor;
    private float centerLineAlpha;

    private boolean displayChoicesMaskFrame;
    private int choiceMaskColor;
    private float choiceMaskAlpha;

    private boolean displayChoicesProgress;
    private int choice1ProgressRectColor;
    private int choice2ProgressRectColor;

    private float pR1X, pR1Wf, pR1Hf; // progressRect1X, WidthFactor, HeightFactor
    private float pR2X, pR2Wf, pR2Hf;

    private OnTouchCallback onTouchCallback;
    private OnSurfaceCreatedCallback onSurfaceCreatedCallback;
    private OnProgressChangeCallback onProgressChangeCallback;
    private OnPicturesStateChangeCallback onPicturesStateChangeCallback;
    private OnDisplayStateChangeCallback onDisplayStateChangeCallback;
    private OnProgressRectClickCallback onProgressRectClickCallback;

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

        picturesVisibility = PICTURES_VISIBLE;

        picturesAlpha = 1f;

        centerLineColor = Color.WHITE;
        centerLineAlpha = 1f;

        displayChoicesMaskFrame = false;
        choiceMaskColor = Color.WHITE;
        choiceMaskAlpha = 0f;

        displayChoicesProgress = false;
        choice1ProgressRectColor = Color.WHITE;
        choice2ProgressRectColor = Color.WHITE;

        displayState = DisplayState.CENTER;

//        layoutRatio = 1.7777778f;

    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        lastNormalizedX = normalizedX;

        onAnimation = false;

        if (null != onTouchCallback)
            onTouchCallback.onTouchPress(normalizedX, normalizedY);
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {

        float progress = Utils.clipProgress(currentProgress + (normalizedX - lastNormalizedX));
        lastNormalizedX = normalizedX;

        onAnimation = false;

        setLayout(progress);

        if (null != onTouchCallback)
            onTouchCallback.onTouchDrag(normalizedX, normalizedY);
    }

    public void handleTouchUp(float normalizedX, float normalizedY) {

        float progress = Utils.clipProgress(currentProgress + (normalizedX - lastNormalizedX));
        lastNormalizedX = normalizedX;

        releaseAnimation(progress);

        setLayout(progress);

        if (displayChoicesProgress) {

            Geometry2D.Point2D p = new Geometry2D.Point2D(normalizedX, normalizedY);

            Geometry2D.Rect2D progressRect1Bounding = new Geometry2D.Rect2D(
                    new Geometry2D.Point2D(pR1X, 0f),
                    pR1Wf * PROGRESS_RECT_WIDTH,
                    pR1Hf * PROGRESS_RECT_HEIGHT);
            boolean progressRect1Click = Geometry2D.intersects(progressRect1Bounding, p);

            Geometry2D.Rect2D progressRect2Bounding = new Geometry2D.Rect2D(
                    new Geometry2D.Point2D(pR2X, 0f),
                    pR2Wf * PROGRESS_RECT_WIDTH,
                    pR2Hf * PROGRESS_RECT_HEIGHT);
            boolean progressRect2Click = Geometry2D.intersects(progressRect2Bounding, p);

            if (null != onProgressRectClickCallback) {
                if (progressRect1Click) {
                    onProgressRectClickCallback.onProgressRect1Click();
                }
                if (progressRect2Click) {
                    onProgressRectClickCallback.onProgressRect2Click();
                }
            }
        }

        if (null != onTouchCallback)
            onTouchCallback.onTouchUp(normalizedX, normalizedY);

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

    public void updateProgressRectAttributes(float progress) {

        if (ANSWER_CHOICE_1 == Utils.getAnswerChoice(progress)) {
            pR1Hf = Utils.map(Math.abs(progress), PROGRESS_CENTER_VALUE, MAX_ABS_PROGRESS_VALUE, PROGRESS_RECT_HEIGHT_CENTER_FACTOR, 1f);
            pR1Wf = Utils.map(Math.abs(progress), PROGRESS_CENTER_VALUE, MAX_ABS_PROGRESS_VALUE, 1f, PROGRESS_RECT_WIDTH_MIN_FACTOR);

            pR2Hf = PROGRESS_RECT_HEIGHT_CENTER_FACTOR;
            pR2Wf = 1f;
        } else {
            pR1Hf = PROGRESS_RECT_HEIGHT_CENTER_FACTOR;
            pR1Wf = 1f;

            pR2Hf = Utils.map(Math.abs(progress), PROGRESS_CENTER_VALUE, MAX_ABS_PROGRESS_VALUE, PROGRESS_RECT_HEIGHT_CENTER_FACTOR, 1f);
            pR2Wf = Utils.map(Math.abs(progress), PROGRESS_CENTER_VALUE, MAX_ABS_PROGRESS_VALUE, 1f, PROGRESS_RECT_WIDTH_MIN_FACTOR);
        }

        pR1X = MIN_NORMALIZED_DEVICE_X + pR1Wf * PROGRESS_RECT_WIDTH / 2;
        pR2X = MAX_NORMALIZED_DEVICE_X - pR2Wf * PROGRESS_RECT_WIDTH / 2;
    }

    public void setOnTouchCallback(OnTouchCallback onTouchCallback) {
        this.onTouchCallback = onTouchCallback;
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

    public void setOnProgressRectClickCallback(OnProgressRectClickCallback onProgressRectClickCallback) {
        this.onProgressRectClickCallback = onProgressRectClickCallback;
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

    public void setCenterLineColor(int centerLineColor) {
        this.centerLineColor = centerLineColor;
    }

    public void setCenterLineAlpha(float centerLineAlpha) {
        this.centerLineAlpha = centerLineAlpha;
    }

    public void setChoiceMaskColor(int choiceMaskColor) {
        this.choiceMaskColor = choiceMaskColor;
    }

    public void setChoiceMaskAlpha(float choiceMaskAlpha) {
        this.choiceMaskAlpha = choiceMaskAlpha;
    }

    public void setDisplayChoicesMaskFrame(boolean displayChoicesMaskFrame) {
        this.displayChoicesMaskFrame = displayChoicesMaskFrame;
    }

    public void setDisplayChoicesProgress(boolean displayChoicesProgress) {
        this.displayChoicesProgress = displayChoicesProgress;
    }

    public void setChoice1ProgressRectColor(int choice1ProgressRectColor) {
        this.choice1ProgressRectColor = choice1ProgressRectColor;
    }

    public void setChoice2ProgressRectColor(int choice2ProgressRectColor) {
        this.choice2ProgressRectColor = choice2ProgressRectColor;
    }

    public void setPicturesVisibility(int visibility) {
        if (View.VISIBLE == visibility) {
            picturesVisibility = PICTURES_VISIBLE;
        } else if (View.INVISIBLE == visibility || View.GONE == visibility) {
            // TODO optim GONE
            picturesVisibility = PICTURES_INVISIBLE;
        } else {
            picturesVisibility = PICTURES_VISIBLE;
        }
    }

    public void setPicturesAlpha(float picturesAlpha) {
        this.picturesAlpha = picturesAlpha;
    }

    public void swapeTextures() {
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

        choiceMaskFrame = new Rect2DFrame(NORMALIZED_DEVICE_MAX_WIDTH, NORMALIZED_DEVICE_MAX_HEIGHT);

        choice1ProgressRect = new Rect2DFrame(PROGRESS_RECT_WIDTH, PROGRESS_RECT_HEIGHT);
        choice2ProgressRect = new Rect2DFrame(PROGRESS_RECT_WIDTH, PROGRESS_RECT_HEIGHT);

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

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Picture 1
        if (isPicture1Ready() && null != textureChoice1Program && null != glPictureChoice1) {

            glPictureChoice1.clipTexture(cw1, ch1);
            positionAndScaleObject2DInScene(x1, 0f, wf1, 1f);
            textureChoice1Program.useProgram();
            textureChoice1Program.setUniforms(modelProjectionMatrix, glPictureChoice1.getTextureId(), picturesAlpha * picturesVisibility);
            glPictureChoice1.bindData(textureChoice1Program);
            glPictureChoice1.draw();
        }

        // Picture 2
        if (isPicture2Ready() && null != textureChoice2Program && null != glPictureChoice2) {
            glPictureChoice2.clipTexture(cw2, ch2);
            positionAndScaleObject2DInScene(x2, 0f, wf2, 1f);
            textureChoice2Program.useProgram();
            textureChoice2Program.setUniforms(modelProjectionMatrix, glPictureChoice2.getTextureId(), picturesAlpha * picturesVisibility);
            glPictureChoice2.bindData(textureChoice2Program);
            glPictureChoice2.draw();
        }


        // CenterLine
        if (null != centerLine && null != colorShaderProgram) {
            // http://stackoverflow.com/questions/11174991/android-opengl-es-1-0-alpha
            positionAndScaleObject2DInScene(centerX, 0f, 1f, 1f);
            colorShaderProgram.useProgram();
            colorShaderProgram.setUniforms(modelProjectionMatrix,
                    (float) Color.red(centerLineColor) / 255,
                    (float) Color.green(centerLineColor) / 255,
                    (float) Color.blue(centerLineColor) / 255,
                    picturesAlpha * centerLineAlpha * picturesVisibility);
            centerLine.bindData(colorShaderProgram);
            centerLine.draw();

        }

        if (displayChoicesMaskFrame && null != choiceMaskFrame && null != colorShaderProgram) {
            positionAndScaleObject2DInScene(0f, 0f, 1f, 1f);
            colorShaderProgram.useProgram();
            colorShaderProgram.setUniforms(modelProjectionMatrix,
                    (float) Color.red(choiceMaskColor) / 255,
                    (float) Color.green(choiceMaskColor) / 255,
                    (float) Color.blue(choiceMaskColor) / 255,
                    choiceMaskAlpha);
            choiceMaskFrame.bindData(colorShaderProgram);
            choiceMaskFrame.draw();
        }

        if (displayChoicesProgress) {
            if (null != choice1ProgressRect && null != colorShaderProgram) {
                positionAndScaleObject2DInScene(pR1X, 0f, pR1Wf, pR1Hf);
                colorShaderProgram.useProgram();
                colorShaderProgram.setUniforms(modelProjectionMatrix,
                        (float) Color.red(choice1ProgressRectColor) / 255,
                        (float) Color.green(choice1ProgressRectColor) / 255,
                        (float) Color.blue(choice1ProgressRectColor) / 255,
                        1f);
                choice1ProgressRect.bindData(colorShaderProgram);
                choice1ProgressRect.draw();
            }

            if (null != choice2ProgressRect && null != colorShaderProgram) {
                positionAndScaleObject2DInScene(pR2X, 0f, pR2Wf, pR2Hf);
                colorShaderProgram.useProgram();
                colorShaderProgram.setUniforms(modelProjectionMatrix,
                        (float) Color.red(choice2ProgressRectColor) / 255,
                        (float) Color.green(choice2ProgressRectColor) / 255,
                        (float) Color.blue(choice2ProgressRectColor) / 255,
                        1f);
                choice2ProgressRect.bindData(colorShaderProgram);
                choice2ProgressRect.draw();
            }
        }

        glDisable(GL_BLEND);

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