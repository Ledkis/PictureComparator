package ledkis.module.picturecomparator.programs;

import android.content.Context;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class ColorShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMatrixLocation;
    private final int uColorLocation;
    
    // Attribute locations
    private final int aPositionLocation;

    private static final String simple_vertex_shader =
            "uniform mat4 u_Matrix;\n" +
                    "attribute vec4 a_Position;\n" +
                    "\n" +
                    "void main(){\n" +
                    "gl_Position = u_Matrix * a_Position;\n" +
                    "}";

    private static final String simple_fragment_shader =
            "precision mediump float;\n" +
                    "uniform vec4 u_Color;\n" +
                    "\n" +
                    "void main(){\n" +
                    "gl_FragColor = u_Color;\n" +
                    "}";


    public ColorShaderProgram(Context context) {
        super(context, simple_vertex_shader, simple_fragment_shader);

        // Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uColorLocation = glGetUniformLocation(program, U_COLOR);
        
        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    public void setUniforms(float[] matrix, float r, float g, float b) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform4f(uColorLocation, r, g, b, 1f);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}
