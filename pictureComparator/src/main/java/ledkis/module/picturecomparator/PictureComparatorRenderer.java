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

public class PictureComparatorRenderer implements Renderer {
    private final Context context;

    private static final float MIN_X = -1f;
    private static final float MAX_X = 1f;
    private static final float MIN_Y = -1f;
    private static final float MAX_Y = 1f;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] modelProjectionMatrix = new float[16];

    private TextureRect2DFrameObject pictureFrame;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    private boolean pictureFrameSelected = false;
    private Point2D pictureFramePosition;
    private Point2D previousPictureFramePosition;

    public PictureComparatorRenderer(Context context) {
        this.context = context;
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {

        Point2D touchedPoint = new Point2D(normalizedX, normalizedY);
        Rect2D pictureFrameBounding = pictureFrame.getRect2D().moveTo(pictureFramePosition);
        pictureFrameSelected = Geometry2D.intersects(pictureFrameBounding, touchedPoint);
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {

        if (pictureFrameSelected) {

            previousPictureFramePosition = pictureFramePosition;
            Point2D touchedPoint = new Point2D(normalizedX, normalizedY);

            pictureFramePosition = new Point2D(
                    clamp(touchedPoint.x,
                            MIN_X + pictureFrame.width / 2,
                            MAX_X - pictureFrame.width / 2),
                    clamp(touchedPoint.y,
                            MIN_Y + pictureFrame.height / 2,
                            MAX_Y - pictureFrame.height / 2));
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

    // The mallets and the puck are positioned on the same plane as the table.
    private void positionObject2DInScene(float x, float y) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, 0f);
        multiplyMM(modelProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);


        pictureFrame = new TextureRect2DFrameObject(0.4f, 0.4f);
        pictureFramePosition = Geometry2D.CENTER_POINT_2D;

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
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

        positionObject2DInScene(pictureFramePosition.x, pictureFramePosition.y);
        textureProgram.useProgram();
        textureProgram.setUniforms(modelProjectionMatrix, texture);
        pictureFrame.bindData(textureProgram);
        pictureFrame.draw();

    }
}