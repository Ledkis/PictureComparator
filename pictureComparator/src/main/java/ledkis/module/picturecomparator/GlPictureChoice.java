package ledkis.module.picturecomparator;

import android.graphics.Bitmap;

import ledkis.module.picturecomparator.objects.TextureRect2DFrameObject;
import ledkis.module.picturecomparator.programs.TextureShaderProgram;
import ledkis.module.picturecomparator.util.GlTexture;
import ledkis.module.picturecomparator.util.TextureChange;
import ledkis.module.picturecomparator.util.Utils;

import static ledkis.module.picturecomparator.Constants.Layout.NO_CLIP;
import static ledkis.module.picturecomparator.Constants.NORMALIZED_DEVICE_MAX_HEIGHT;
import static ledkis.module.picturecomparator.Constants.NORMALIZED_DEVICE_MAX_WIDTH;

public class GlPictureChoice {

    public static final String TAG = "GlPictureChoice";

    private TextureRect2DFrameObject textureRect2DFrameObject;
    private GlTexture glTexture;

    private TextureChange textureChange;

    public GlPictureChoice() {
    }

    public void initTexture() {
        if (!isTextureInitialized())
            glTexture = new GlTexture();
    }

    private void onTextureLoaded(float layoutRatio) {

        float textureRatio = getTextureRatio();
        float picW, picH;

        if (textureRatio > layoutRatio) {
            picW = (NORMALIZED_DEVICE_MAX_HEIGHT / textureRatio) * layoutRatio;
            picH = NORMALIZED_DEVICE_MAX_HEIGHT;
        } else {
            picW = NORMALIZED_DEVICE_MAX_WIDTH;
            picH = (NORMALIZED_DEVICE_MAX_WIDTH / textureRatio) * layoutRatio;
        }

        textureRect2DFrameObject = new TextureRect2DFrameObject(picW, picH, NO_CLIP, NO_CLIP);

        Utils.v(TAG, "textureLoaded: " + glTexture.getId() + ", " + picW + "x" + picH);
    }

    public void deleteTexture() {
        if (isFrameInitialized()) {
            glTexture.getBitmap().recycle();
            glTexture.setBitmap(null);
            textureRect2DFrameObject = null;
        }
    }

    public boolean updateBitmap(float layoutRatio) {
        boolean changed = glTexture.updateBitmap(textureChange);

        if (changed) {
            onTextureLoaded(layoutRatio);
            textureChange = null;
        }

        return changed;
    }

    public void setTextureChange(TextureChange textureChange) {
        this.textureChange = textureChange;
    }

    public void clipTexture(float clipWidthFactor, float clipHeightFactor) {
        textureRect2DFrameObject.clipTexture(clipWidthFactor, clipHeightFactor);
    }

    public void bindData(TextureShaderProgram textureChoiceProgram) {
        textureRect2DFrameObject.bindData(textureChoiceProgram);
    }

    public void draw() {
        textureRect2DFrameObject.draw();
    }

    public float getTextureRatio() {
        if (null != glTexture)
            return (float) glTexture.getHeight() / (float) glTexture.getWidth();
        else
            return -1;
    }

    public int getTextureId() {
        if (null != glTexture)
            return glTexture.getId();
        else
            return -1;
    }

    public boolean isFrameInitialized() {
        return null != textureRect2DFrameObject;
    }

    public Bitmap getTextureBitmap() {
        return glTexture.getBitmap();
    }

    public float getWidth() {
        return textureRect2DFrameObject.getWidth();
    }

    public float getHeight() {
        return textureRect2DFrameObject.getHeight();
    }

    public boolean isTextureInitialized() {
        return null != glTexture && glTexture.isTextureInitialized();
    }

}
