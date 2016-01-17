package ledkis.module.picturecomparator;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ledkis.module.picturecomparator.objects.TextureRect2DFrameObject;
import ledkis.module.picturecomparator.programs.TextureShaderProgram;
import ledkis.module.picturecomparator.util.TextureHelper;
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
import static ledkis.module.picturecomparator.Constants.Layout.CENTER_CLIP;
import static ledkis.module.picturecomparator.Constants.Layout.CENTER_WIDTH;
import static ledkis.module.picturecomparator.Constants.Layout.CHOICE1_START_X;
import static ledkis.module.picturecomparator.Constants.Layout.CHOICE2_START_X;
import static ledkis.module.picturecomparator.Constants.Layout.MAX_ABS_PROGRESS_VALUE;
import static ledkis.module.picturecomparator.Constants.Layout.NO_CLIP;
import static ledkis.module.picturecomparator.Constants.Layout.PROGRESS_CENTER_VALUE;
import static ledkis.module.picturecomparator.Constants.MAX_NORMALIZED_DEVICE_X;
import static ledkis.module.picturecomparator.Constants.MIN_NORMALIZED_DEVICE_X;
import static ledkis.module.picturecomparator.Constants.NORMALIZED_DEVICE_MAX_HEIGHT;
import static ledkis.module.picturecomparator.Constants.NORMALIZED_DEVICE_MAX_WIDTH;
import static ledkis.module.picturecomparator.util.TextureHelper.TextureInfo;

public class PictureComparatorRenderer implements Renderer {

    public interface Callback {

        void onHandleTouchPress(float normalizedX, float normalizedY);

        void onHandleTouchDrag(float normalizedX, float normalizedY);

        void onHandleTouchUp(float normalizedX, float normalizedY);

        void onProgressChange(float progress);
    }


    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] modelProjectionMatrix = new float[16];

    private TextureRect2DFrameObject choice1Picture;
    private TextureRect2DFrameObject choice2Picture;

    private TextureInfo choice1TextureInfo;
    private TextureInfo choice2TextureInfo;

    private TextureShaderProgram textureChoice1Program;
    private TextureShaderProgram textureChoice2Program;

    private float currentProgress;
    private float lastNormalizedX;

    private float screenRatio;

    private float pic1Ratio, pic2Ratio, pic1W, pic1H, pic2W, pic2H;

    float x1, x2,     // position
            w1, w2,   // width
            wf1, wf2, // width factor
            cw1, cw2, // clip width factor
            ch1, ch2; // clip height factor

    private Callback callback;

    private GLSurfaceView glSurfaceView;

    public PictureComparatorRenderer(Context context, GLSurfaceView glSurfaceView) {
        this.context = context;
        this.glSurfaceView = glSurfaceView;
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        lastNormalizedX = normalizedX;

        if (null != callback)
            callback.onHandleTouchPress(normalizedX, normalizedY);
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {

        float progress = Utils.clipProgress(currentProgress + (normalizedX - lastNormalizedX));
        lastNormalizedX = normalizedX;

        setLayout(progress);

        if (null != callback)
            callback.onHandleTouchDrag(normalizedX, normalizedY);
    }

    public void handleTouchUp(float normalizedX, float normalizedY) {

        float progress = Utils.clipProgress(currentProgress + (normalizedX - lastNormalizedX));
        lastNormalizedX = normalizedX;

        setLayout(progress);

        if (null != callback)
            callback.onHandleTouchUp(normalizedX, normalizedY);

    }

    private void setCurrentProgress(float progress) {
        currentProgress = progress;
        if (null != callback)
            callback.onProgressChange(currentProgress);
    }

    public void setLayout(float progress) {
        setCurrentProgress(progress);

        evalPicPosition(progress);
        evalPicWidth(progress);
        evalPicClipping(w1, w2);
    }

    private void evalPicPosition(float progress) {
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

        wf1 = w1 / pic1W;
        wf2 = w2 / pic2W;
    }

    private void evalPicClipping(float pic1Width, float pic2Width) {

        // Pic1
        float choice1Ratio = (NORMALIZED_DEVICE_MAX_WIDTH * NORMALIZED_DEVICE_MAX_HEIGHT) / (pic1Width);

        cw1 = NO_CLIP;
        ch1 = (0.5f * pic1Ratio * pic1Width) / NORMALIZED_DEVICE_MAX_HEIGHT;

        if (pic1Ratio > choice1Ratio) {
            cw1 = NO_CLIP;
            ch1 = NORMALIZED_DEVICE_MAX_HEIGHT / (0.5f * pic1Ratio * pic1Width);
        } else {
            cw1 = (0.5f * pic1Ratio * pic1Width) / (NORMALIZED_DEVICE_MAX_WIDTH);
            ch1 = NO_CLIP;
        }

        // Pic2
        float choice2Ratio = (NORMALIZED_DEVICE_MAX_WIDTH * NORMALIZED_DEVICE_MAX_HEIGHT) /
                (pic2Width);

        cw2 = NO_CLIP;
        ch2 = (0.5f * pic2Ratio * pic2Width) / NORMALIZED_DEVICE_MAX_HEIGHT;

        if (pic2Ratio > choice2Ratio) {
            cw2 = NO_CLIP;
            ch2 = NORMALIZED_DEVICE_MAX_HEIGHT / (0.5f * pic2Ratio * pic2Width);
        } else {
            cw2 = (0.5f * pic2Ratio * pic2Width) / (NORMALIZED_DEVICE_MAX_WIDTH);
            ch2 = NO_CLIP;
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
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

        screenRatio = (float) glSurfaceView.getHeight() / (float) glSurfaceView.getWidth();

        textureChoice1Program = new TextureShaderProgram(context);
        textureChoice2Program = new TextureShaderProgram(context);
        choice1TextureInfo = TextureHelper.loadTexture(context, R.drawable.choice1);
        choice2TextureInfo = TextureHelper.loadTexture(context, R.drawable.choice2);

        pic1Ratio = (float) choice1TextureInfo.height / (float) choice1TextureInfo.width;
        pic2Ratio = (float) choice2TextureInfo.height / (float) choice2TextureInfo.width;

        if (pic1Ratio > screenRatio) {
            pic1W = (NORMALIZED_DEVICE_MAX_HEIGHT / pic1Ratio) * screenRatio;
            pic1H = NORMALIZED_DEVICE_MAX_HEIGHT;
        } else {
            pic1W = NORMALIZED_DEVICE_MAX_WIDTH;
            pic1H = (NORMALIZED_DEVICE_MAX_WIDTH / pic1Ratio) * screenRatio;
        }

        if (pic2Ratio > screenRatio) {
            pic2W = (NORMALIZED_DEVICE_MAX_HEIGHT / pic2Ratio) * screenRatio;
            pic2H = NORMALIZED_DEVICE_MAX_HEIGHT;
        } else {
            pic2W = NORMALIZED_DEVICE_MAX_WIDTH;
            pic2H = (NORMALIZED_DEVICE_MAX_WIDTH / pic2Ratio) * screenRatio;
        }

        choice1Picture = new TextureRect2DFrameObject(pic1W, pic1H, NO_CLIP, NO_CLIP);
        choice2Picture = new TextureRect2DFrameObject(pic2W, pic2H, CENTER_CLIP, NO_CLIP);

        setLayout(PROGRESS_CENTER_VALUE);
    }

    @Override

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        orthoM(projectionMatrix, 0, -1f, 1f, -1f, 1f, -1f, 1f);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        choice1Picture.clipTexture(cw1, ch1);
        choice2Picture.clipTexture(cw2, ch2);

        positionAndScaleObject2DInScene(x1, 0f, wf1, 1f);
        textureChoice1Program.useProgram();
        textureChoice1Program.setUniforms(modelProjectionMatrix, choice1TextureInfo.id);
        choice1Picture.bindData(textureChoice1Program);
        choice1Picture.draw();

        // Picture 2
        positionAndScaleObject2DInScene(x2, 0f, wf2, 1f);
        textureChoice2Program.useProgram();
        textureChoice2Program.setUniforms(modelProjectionMatrix, choice2TextureInfo.id);
        choice2Picture.bindData(textureChoice2Program);
        choice2Picture.draw();
    }
}