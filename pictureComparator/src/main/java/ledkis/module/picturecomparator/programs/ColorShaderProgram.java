package ledkis.module.picturecomparator.programs;

import android.content.Context;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class ColorShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMatrixLocation;
    private final int uColorLocation;
    private final int uAlphaLocation;
    private final int uBrightnessLocation;

    // Attribute locations
    private final int aPositionLocation;

    private static final String SIMPLE_VERTEX_SHADER =
            "uniform mat4 u_Matrix;\n" +
                    "attribute vec4 a_Position;\n" +
                    "\n" +
                    "void main(){\n" +
                    "gl_Position = u_Matrix * a_Position;\n" +
                    "}";

    private static final String SIMPLE_FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "uniform vec4 u_Color;\n" +
                    "uniform float u_Alpha;\n" +
                    "uniform float u_Brightness;\n" +
                    "\n" +
                    "void main(){\n" +
                    "vec4 v4Colour = u_Color * u_Alpha;\n" +
                    "v4Colour.rgb += u_Brightness;\n" +
                    "gl_FragColor = v4Colour;\n" +
                    "}";


    public ColorShaderProgram(Context context) {
        super(context, SIMPLE_VERTEX_SHADER, SIMPLE_FRAGMENT_SHADER);

        // Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uColorLocation = glGetUniformLocation(program, U_COLOR);

        uAlphaLocation = glGetUniformLocation(program, U_ALPHA);

        uBrightnessLocation = glGetUniformLocation(program, U_BRIGHTNESS);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    public void setUniforms(float[] matrix, float r, float g, float b, float alpha, float brightness) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform4f(uColorLocation, r, g, b, 1f);
        glUniform1f(uAlphaLocation, alpha);
        glUniform1f(uBrightnessLocation, brightness);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}
