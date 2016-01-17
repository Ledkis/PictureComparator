package ledkis.module.picturecomparator;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ledkis.module.picturecomparator.objects.TextureRect2DFrameObject;
import ledkis.module.picturecomparator.programs.TextureShaderProgram;
import ledkis.module.picturecomparator.util.Geometry2D.Point2D;
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
import static ledkis.module.picturecomparator.Constants.Layout.CENTER_CLIP;
import static ledkis.module.picturecomparator.Constants.Layout.MAX_ABS_PROGRESS_VALUE;
import static ledkis.module.picturecomparator.Constants.Layout.NO_CLIP;
import static ledkis.module.picturecomparator.Constants.Layout.PROGRESS_CENTER_VALUE;
import static ledkis.module.picturecomparator.Constants.MAX_NORMALIZED_DEVICE_X;
import static ledkis.module.picturecomparator.Constants.MAX_NORMALIZED_DEVICE_Y;
import static ledkis.module.picturecomparator.Constants.MIN_NORMALIZED_DEVICE_X;
import static ledkis.module.picturecomparator.Constants.MIN_NORMALIZED_DEVICE_Y;
import static ledkis.module.picturecomparator.Constants.NORMALIZED_DEVICE_MAX_HEIGHT;
import static ledkis.module.picturecomparator.Constants.NORMALIZED_DEVICE_MAX_WIDTH;
import static ledkis.module.picturecomparator.util.TextureHelper.TextureInfo;

public class PictureComparatorRenderer implements Renderer {
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
            w1, w2, // width ratio
            wf1, wf2, // width ratio
            cw1, cw2, // clip width
            ch1, ch2; // clip height

    public PictureComparatorRenderer(Context context, float screenRatio) {
        this.context = context;

        this.screenRatio = screenRatio;
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        lastNormalizedX = normalizedX;
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {

        currentProgress = Utils.clipProgress(currentProgress + (normalizedX - lastNormalizedX));
        lastNormalizedX = normalizedX;

//        if (ANSWER_CHOICE_1 == Utils.getAnswerChoice(currentProgress)) {
//            x1 = Utils.map(Math.abs(currentProgress), PROGRESS_CENTER_VALUE,
//                    MAX_ABS_PROGRESS_VALUE, CHOICE1_START_X, CENTER_CHOICE_X);
//            x2 = Utils.map(Math.abs(currentProgress), PROGRESS_CENTER_VALUE,
//                    MAX_ABS_PROGRESS_VALUE, CHOICE2_START_X, MAX_NORMALIZED_DEVICE_X);
//
//            wf1 = Utils.map(Math.abs(currentProgress), PROGRESS_CENTER_VALUE,
//                    MAX_ABS_PROGRESS_VALUE, pic1W, NORMALIZED_DEVICE_MAX_WIDTH);
//            wf2 = Utils.map(Math.abs(currentProgress), PROGRESS_CENTER_VALUE,
//                    MAX_ABS_PROGRESS_VALUE, pic2W, 0f);
//
//        } else {
//            x1 = Utils.map(Math.abs(currentProgress), PROGRESS_CENTER_VALUE,
//                    MAX_ABS_PROGRESS_VALUE, CHOICE1_START_X, MIN_NORMALIZED_DEVICE_X);
//            x2 = Utils.map(Math.abs(currentProgress), PROGRESS_CENTER_VALUE,
//                    MAX_ABS_PROGRESS_VALUE, CHOICE2_START_X, CENTER_CHOICE_X);
//
//            wf1 = Utils.map(Math.abs(currentProgress), PROGRESS_CENTER_VALUE,
//                    MAX_ABS_PROGRESS_VALUE, pic1W, 0f);
//            wf2 = Utils.map(Math.abs(currentProgress), PROGRESS_CENTER_VALUE,
//                    MAX_ABS_PROGRESS_VALUE, pic2W, NORMALIZED_DEVICE_MAX_WIDTH);
//
//        }
//
//        float choice1Ratio = NORMALIZED_DEVICE_MAX_HEIGHT / wf1;
//
//        if(pic1Ratio > choice1Ratio){
//            cw1 = NO_CLIP;
//            ch1 = (NORMALIZED_DEVICE_MAX_HEIGHT*100) / choice1TextureInfo.height;
//        } else {
//            cw1 = (NORMALIZED_DEVICE_MAX_WIDTH*100) / choice1TextureInfo.width;
//            ch1 = NO_CLIP;
//        }
//
//        float choice2Ratio = NORMALIZED_DEVICE_MAX_HEIGHT / wf2;
//
//        if(pic2Ratio > choice2Ratio){
//            cw2 = NO_CLIP;
//            ch2 = (NORMALIZED_DEVICE_MAX_HEIGHT*100) / choice2TextureInfo.height;
//        } else {
//            cw2 = (NORMALIZED_DEVICE_MAX_WIDTH*100) / choice2TextureInfo.width;
//            ch2 = NO_CLIP;
//        }
//
//        choice1Picture.clipTexture(cw1, ch1);
//        choice2Picture.clipTexture(cw2, ch2);
//        Log.d("XXX", "wf1: " + wf1);
//
//        wf1 = (NORMALIZED_DEVICE_MAX_WIDTH*100)/wf1;
//        wf2 = (NORMALIZED_DEVICE_MAX_WIDTH*100)/wf1;


        // Picture 1


//        if (ANSWER_CHOICE_1 == Utils.getAnswerChoice(currentProgress)) {
//            w1 = Utils.map(Math.abs(currentProgress), PROGRESS_CENTER_VALUE,
//                    MAX_ABS_PROGRESS_VALUE, pic1W, NORMALIZED_DEVICE_MAX_WIDTH);
//        } else {
//            w1 = Utils.map(Math.abs(currentProgress), PROGRESS_CENTER_VALUE,
//                    MAX_ABS_PROGRESS_VALUE, pic1W, 0f);
//        }

        w1 = Utils.map(Math.abs(currentProgress), PROGRESS_CENTER_VALUE,
                MAX_ABS_PROGRESS_VALUE, 2 * NORMALIZED_DEVICE_MAX_WIDTH, 0f);

        wf1 = w1 / pic1W;

        float choice1Ratio = (NORMALIZED_DEVICE_MAX_WIDTH * NORMALIZED_DEVICE_MAX_HEIGHT) / (w1);


        cw1 = NO_CLIP;
        ch1 = (0.5f * pic1Ratio * w1) / NORMALIZED_DEVICE_MAX_HEIGHT;

        if (pic1Ratio > choice1Ratio) {
            cw1 = NO_CLIP;
            ch1 = NORMALIZED_DEVICE_MAX_HEIGHT / (0.5f * pic1Ratio * w1);
        } else {
            cw1 = (0.5f * pic1Ratio * w1) / (NORMALIZED_DEVICE_MAX_WIDTH);
            ch1 = NO_CLIP;
        }

        Log.d("XXX", "currentProgress: " + Math.abs(currentProgress) +
                        ", w1: " + w1 +
                        ", wf1: " + wf1 +
                        ", cw1: " + cw1 +
                        ", ch1: " + ch1 + "" +
                        ", pic1Ratio: " + pic1Ratio +
                        ", choice1Ratio: " + choice1Ratio

        );

    }

    private Point2D getNewPosition(float normalizedX, float normalizedY) {
        Point2D touchedPoint = new Point2D(normalizedX, normalizedY);

        return new Point2D(
                Utils.clamp(touchedPoint.x,
                        MIN_NORMALIZED_DEVICE_X + choice1Picture.width / 2,
                        MAX_NORMALIZED_DEVICE_X - choice1Picture.width / 2),
                Utils.clamp(touchedPoint.y,
                        MIN_NORMALIZED_DEVICE_Y + choice1Picture.height / 2,
                        MAX_NORMALIZED_DEVICE_Y - choice1Picture.height / 2));
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

        currentProgress = PROGRESS_CENTER_VALUE;
        lastNormalizedX = PROGRESS_CENTER_VALUE;

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

        Log.d("XXX", "pic1W: " + pic1W);
        Log.d("XXX", "pic1H: " + pic1H);
        Log.d("XXX", "pic1Ratio: " + pic1Ratio);

        choice1Picture = new TextureRect2DFrameObject(pic1W, pic1H, NO_CLIP, NO_CLIP);
        choice2Picture = new TextureRect2DFrameObject(pic2W, pic2H, CENTER_CLIP, NO_CLIP);

    }

    @Override

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

//        // create  an  orthographic  projection  matrix
//        final float aspectRatio = width > height ?
//                (float) width / (float) height :
//                (float) height / (float) width;
//
//        if (width > height) {
//            // Landscape
//            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
//        } else {
//            // Portrait or square
//            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
//        }

        orthoM(projectionMatrix, 0, -1f, 1f, -1f, 1f, -1f, 1f);

    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);


        choice1Picture.clipTexture(cw1, ch1);

        positionAndScaleObject2DInScene(0f, 0f, wf1, 1f);
        textureChoice1Program.useProgram();
        textureChoice1Program.setUniforms(modelProjectionMatrix, choice1TextureInfo.id);
        choice1Picture.bindData(textureChoice1Program);
        choice1Picture.draw();

        // Picture 2
//        positionAndScaleObject2DInScene(x2, 0f, wf2, 1f);
//        textureChoice2Program.useProgram();
//        textureChoice2Program.setUniforms(modelProjectionMatrix, choice2TextureInfo.id);
//        choice2Picture.bindData(textureChoice2Program);
//        choice2Picture.draw();

    }
}