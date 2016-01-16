package ledkis.module.picturecomparator;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ledkis.module.picturecomparator.objects.TextureRect2DFrameObject;
import ledkis.module.picturecomparator.programs.ColorShaderProgram;
import ledkis.module.picturecomparator.programs.TextureShaderProgram;
import ledkis.module.picturecomparator.util.Geometry2D;
import ledkis.module.picturecomparator.util.Geometry2D.Point2D;
import ledkis.module.picturecomparator.util.Geometry2D.Rect2D;
import ledkis.module.picturecomparator.util.TextureHelper;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static ledkis.module.picturecomparator.Constants.MAX_NORMALIZED_DEVICE_X;
import static ledkis.module.picturecomparator.Constants.MAX_NORMALIZED_DEVICE_Y;
import static ledkis.module.picturecomparator.Constants.MIN_NORMALIZED_DEVICE_X;
import static ledkis.module.picturecomparator.Constants.MIN_NORMALIZED_DEVICE_Y;

public class PictureComparatorRenderer implements Renderer {
    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] modelProjectionMatrix = new float[16];

    private TextureRect2DFrameObject choice1Picture;
    private TextureRect2DFrameObject choice2Picture;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int textureChoice1;
    private int textureChoice2;

    private boolean choice1Selected = false;
    private boolean choice2Selected = false;
    private Point2D choice1PicturePosition;
    private Point2D choice2PicturePosition;

    public PictureComparatorRenderer(Context context) {
        this.context = context;
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {

        Point2D touchedPoint = new Point2D(normalizedX, normalizedY);

        Rect2D choice2PictureBounding = choice2Picture.getRect2D().moveTo(choice2PicturePosition);
        choice2Selected = Geometry2D.intersects(choice2PictureBounding, touchedPoint);

        if (choice2Selected)
            return;

        Rect2D choice1PictureBounding = choice1Picture.getRect2D().moveTo(choice1PicturePosition);
        choice1Selected = Geometry2D.intersects(choice1PictureBounding, touchedPoint);
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {


        if (choice2Selected) {
            choice2PicturePosition = getNewPosition(normalizedX, normalizedY);
        } else if (choice1Selected) {
            choice1PicturePosition = getNewPosition(normalizedX, normalizedY);
        }


    }

    private Point2D getNewPosition(float normalizedX, float normalizedY) {
        Point2D touchedPoint = new Point2D(normalizedX, normalizedY);

        return new Point2D(
                clamp(touchedPoint.x,
                        MIN_NORMALIZED_DEVICE_X + choice1Picture.width / 2,
                        MAX_NORMALIZED_DEVICE_X - choice1Picture.width / 2),
                clamp(touchedPoint.y,
                        MIN_NORMALIZED_DEVICE_Y + choice1Picture.height / 2,
                        MAX_NORMALIZED_DEVICE_Y - choice1Picture.height / 2));
    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

    private void positionObject2DInScene(float x, float y) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, 0f);
        multiplyMM(modelProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);


        choice1Picture = new TextureRect2DFrameObject(0.4f, 0.8f);
        choice1PicturePosition = new Point2D(-0.5f, 0f);

        choice2Picture = new TextureRect2DFrameObject(0.4f, 0.8f);
        choice2PicturePosition = new Point2D(0.5f, 0f);

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        textureChoice1 = TextureHelper.loadTexture(context, R.drawable.choice1);
        textureChoice2 = TextureHelper.loadTexture(context, R.drawable.choice2);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        // create  an  orthographic  projection  matrix
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        if (width > height) {
            // Landscape
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // Portrait or square
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }

    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        // Update the viewProjection matrix, and create an inverted matrix for
        // touch picking.

        positionObject2DInScene(choice1PicturePosition.x, choice1PicturePosition.y);
        textureProgram.useProgram();
        textureProgram.setUniforms(modelProjectionMatrix, textureChoice1);
        choice1Picture.bindData(textureProgram);
        choice1Picture.draw();

        positionObject2DInScene(choice2PicturePosition.x, choice2PicturePosition.y);
        textureProgram.setUniforms(modelProjectionMatrix, textureChoice2);
        choice2Picture.bindData(colorProgram);
        choice2Picture.draw();

    }
}