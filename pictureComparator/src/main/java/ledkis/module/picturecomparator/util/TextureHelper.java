package ledkis.module.picturecomparator.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import ledkis.module.picturecomparator.Constants;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

public class TextureHelper {
    private static final String TAG = "TextureHelper";

    /**
     * http://stackoverflow.com/questions/30140178/opengl-es-2-0-get-texture-size-and-other-info
     */
    public static class TextureInfo {

        public final int id;
        public final int width;
        public final int height;

        public TextureInfo(int id, int width, int height) {
            this.id = id;
            this.width = width;
            this.height = height;
        }
    }

    /**
     * Loads a texture from a resource ID, returning the OpenGL ID for that
     * texture. Returns 0 if the load failed.
     * 
     * @param context
     * @param resourceId
     * @return
     */
    public static TextureInfo loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (Constants.LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }

            return new TextureInfo(0, -1, -1);
        }
        
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(
            context.getResources(), resourceId, options);

        if (bitmap == null) {
            if (Constants.LoggerConfig.ON) {
                Log.w(TAG, "Resource ID " + resourceId
                    + " could not be decoded.");
            }

            glDeleteTextures(1, textureObjectIds, 0);

            return new TextureInfo(0, -1, -1);
        } 
        
        // Bind to the texture in OpenGL
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // Set filtering: a default must be set, or the texture will be
        // black.
        glTexParameteri(GL_TEXTURE_2D,
            GL_TEXTURE_MIN_FILTER,
            GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,
                GL_TEXTURE_MAG_FILTER, GL_LINEAR);
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

        TextureInfo textureInfo = new TextureInfo(textureObjectIds[0], bitmap.getWidth(), bitmap
                .getHeight());

        // Recycle the bitmap, since its data has been loaded into
        // OpenGL.
        bitmap.recycle();

        // Unbind from the texture.
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureInfo;
    }
}
