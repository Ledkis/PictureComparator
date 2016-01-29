package ledkis.module.picturecomparator.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import java.io.IOException;
import java.io.InputStream;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

public class GlTexture {

    public static final String TAG = "GlTexture";

    /**
     * http://stackoverflow.com/questions/30140178/opengl-es-2-0-get-texture-size-and-other-info
     */

    public static final int NOT_INIT = 0;

    private int id;
    private int width;
    private int height;

    private Bitmap bitmap;

    public GlTexture() {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == NOT_INIT) {
            Utils.w(TAG, "Could not generate a new OpenGL texture object.");

            unInit();
        }

        id = textureObjectIds[0];

        Utils.v(TAG, "New texture init: " + id);
    }

    public void unInit() {
        id = NOT_INIT;
        setBitmap(null);
    }

    public int getId() {
        return id;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (null != bitmap) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        } else {
            width = -1;
            height = -1;
        }
    }

    public boolean isBitmapLoaded() {
        return null != bitmap;
    }

    public boolean isTextureInitialized() {
        return id != NOT_INIT;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void bindTexture(final Bitmap bitmap, Object foo) {

        if (!isTextureInitialized()) {
            Utils.w(TAG, "Could not bind the texture : not initialized");
            unInit();
            return;
        }

        if (bitmap == null) {
            Utils.w(TAG, "Could not bind the texture : Bitmap is null.");

            glDeleteTextures(1, new int[]{getId()}, 0);

            unInit();

            return;
        }


        // Bind to the texture in OpenGL
        glBindTexture(GL_TEXTURE_2D, getId());

        initTextureParams();

        // Load the bitmap into the bound texture.
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        // Note: Following code may cause an error to be reported in the
        // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
        // Failed to generate texture mipmap levels (error=3)
        // No OpenGL error will be encountered (glGetError() will return
        // 0). If this happens, just squash the source image to be
        // square. It will look the same because of texture coordinates,
        // and mipmap generation will work.

        glGenerateMipmap(GL_TEXTURE_2D);

        if (isBitmapLoaded()) {
            Utils.v(TAG, "Texture changed: " + bitmap.getWidth() + "x" + bitmap.getHeight());
        }

        setBitmap(bitmap);

        // Recycle the bitmap, since its data has been loaded into
        // OpenGL.
        // We don't recycle in case we swape pictures : we need the bitmap
//            bitmap.recycle();

        // Unbind from the texture.
        glBindTexture(GL_TEXTURE_2D, 0);


    }

    /**
     * Loads a texture from a resource ID, returning the OpenGL ID for that
     * texture. Returns 0 if the load failed.
     *
     * @param context
     * @param resourceId
     * @return
     */
    private void bindTexture(Context context, int resourceId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);

        if (bitmap == null) {
            Utils.w(TAG, "Resource ID " + resourceId
                    + " could not be decoded.");
            return;
        }

        bindTexture(bitmap, null);

        Utils.w(TAG, "Res texture bind: " + width + "x" + height);
    }

    private void bindTexture(final byte[] picturesBytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(picturesBytes, 0, picturesBytes.length);
        bindTexture(bitmap, null);

        Utils.w(TAG, "Bytes texture bind: " + width + "x" + height);
    }

    private void bindTexture(final String filePath, int reqWidth, int reqHeight) {
        Utils.w(TAG, "Load path texture");
        Bitmap bitmap = Utils.decodeSampledBitmap(filePath, reqWidth, reqHeight);
        bindTexture(bitmap, null);

        Utils.w(TAG, "Path texture bind: " + width + "x" + height);
    }

    private void bindTexture(Context context, final String imageAssetName) {
        try {
            AssetManager as = context.getResources().getAssets();
            InputStream is = as.open(imageAssetName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            bindTexture(bitmap, null);

            Utils.w(TAG, "Assets texture bind: " + width + "x" + height);
        } catch (IOException ex) {
            unInit();
        }
    }

    private void bindTexture(final Bitmap bitmap) {
        bindTexture(bitmap, null);

        Utils.w(TAG, "Bitmap texture bind: " + width + "x" + height);
    }

    public boolean updateBitmap(TextureChange textureChange) {
        if (null == textureChange)
            return false;

        if (textureChange.getResourceId() != 0) {
            bindTexture(textureChange.getContext(), textureChange.getResourceId());
        } else if (textureChange.getPicturesBytes() != null) {
            bindTexture(textureChange.getPicturesBytes());
        } else if (textureChange.getBitmap() != null) {
            bindTexture(textureChange.getBitmap());
        } else if (textureChange.getFilePath() != null) {
            bindTexture(textureChange.getFilePath(), textureChange.getReqWidth(), textureChange.getReqHeight());
        } else if (textureChange.getImageAssetName() != null) {
            bindTexture(textureChange.getContext(), textureChange.getImageAssetName());
        }

        return true;
    }

    private static void initTextureParams() {
        // Set filtering: a default must be set, or the texture will be
        // black.

        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }
}

