package ledkis.module.picturecomparator;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ledkis.module.picturecomparator.objects.TextureRect2DFrameObject;
import ledkis.module.picturecomparator.programs.TextureShaderProgram;
import ledkis.module.picturecomparator.util.Geometry2D;
import ledkis.module.picturecomparator.util.Geometry2D.Point2D;
import ledkis.module.picturecomparator.util.Geometry2D.Rect2D;
import ledkis.module.picturecomparator.util.TextureHelper;
import ledkis.module.picturecomparator.util.Utils;

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
import static ledkis.module.picturecomparator.Constants.NO_CLIP;
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


//        if (choice2Selected) {
//            choice2PicturePosition = getNewPosition(normalizedX, normalizedY);
//        } else if (choice1Selected) {
//            choice1PicturePosition = getNewPosition(normalizedX, normalizedY);
//        }

        float clipValue = Utils.map(normalizedX, -1f, 1f, 0f, 1f);
        choice1Picture.clipTexture(clipValue);

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

    private void positionObject2DInScene(float x, float y) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, 0f);
        multiplyMM(modelProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        textureChoice1Program = new TextureShaderProgram(context);
        textureChoice2Program = new TextureShaderProgram(context);
        choice1TextureInfo = TextureHelper.loadTexture(context, R.drawable.choice1);
        choice2TextureInfo = TextureHelper.loadTexture(context, R.drawable.choice2);


        choice1Picture = new TextureRect2DFrameObject(1f, 2f, NO_CLIP);
        choice1PicturePosition = new Point2D(-0.5f, 0f);

        choice2Picture = new TextureRect2DFrameObject(1f, 2f, NO_CLIP);
        choice2PicturePosition = new Point2D(0.5f, 0f);



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

        // Picture 1
        positionObject2DInScene(choice1PicturePosition.x, choice1PicturePosition.y);
        textureChoice1Program.useProgram();
        textureChoice1Program.setUniforms(modelProjectionMatrix, choice1TextureInfo.id);
        choice1Picture.bindData(textureChoice1Program);
        choice1Picture.draw();

        // Picture 2
        positionObject2DInScene(choice2PicturePosition.x, choice2PicturePosition.y);
        textureChoice2Program.useProgram();
        textureChoice2Program.setUniforms(modelProjectionMatrix, choice2TextureInfo.id);
        choice2Picture.bindData(textureChoice2Program);
        choice2Picture.draw();

    }
}