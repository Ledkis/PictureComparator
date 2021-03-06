package ledkis.module.picturecomparator.programs;

import android.content.Context;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class TextureShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    private final int uAlphaLocation;
    private final int uBrightnessLocation;
    private final int uContrastLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    private static final String TEXTURE_VERTEX_SHADER =
            "uniform mat4 u_Matrix;\n" +
                    "attribute vec4 a_Position;\n" +
                    "attribute vec2 a_TextureCoordinates;\n" +
                    "varying vec2 v_TextureCoordinates;\n" +
                    "\n" +
                    "void main(){\n" +
                    "v_TextureCoordinates = a_TextureCoordinates;\n" +
                    "gl_Position = u_Matrix * a_Position;\n" +
                    "}";

    // http://stackoverflow.com/questions/944713/help-with-pixel-shader-effect-for-brightness-and-contrast
    private static final String TEXTURE_FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "uniform sampler2D u_TextureUnit;\n" +
                    "varying vec2 v_TextureCoordinates;\n" +
                    "uniform float u_Alpha;\n" +
                    "uniform float u_Brightness;\n" +
                    "uniform float u_Contrast;\n" +
                    "\n" +
                    "void main(){\n" +
                    "vec4 v4Colour = texture2D(u_TextureUnit, v_TextureCoordinates);\n" +
                    "v4Colour.a = u_Alpha;\n" +
                    "v4Colour.rgb /= v4Colour.a;\n" +
                    "v4Colour.rgb = (v4Colour.rgb - 0.5) * u_Contrast + 0.5;\n" +
                    "v4Colour.rgb += u_Brightness;\n" +
                    "v4Colour.rgb *= v4Colour.a;\n" +
                    "gl_FragColor = v4Colour;\n" +
                    "}";

    public TextureShaderProgram(Context context) {
        super(context, TEXTURE_VERTEX_SHADER, TEXTURE_FRAGMENT_SHADER);

        // Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        uAlphaLocation = glGetUniformLocation(program, U_ALPHA);

        uBrightnessLocation = glGetUniformLocation(program, U_BRIGHTNESS);

        uContrastLocation = glGetUniformLocation(program, U_CONTRAST);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] matrix, int textureId, float alpha, float brightness, float contrast) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uAlphaLocation, alpha);
        glUniform1f(uBrightnessLocation, brightness);
        glUniform1f(uContrastLocation, contrast);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}