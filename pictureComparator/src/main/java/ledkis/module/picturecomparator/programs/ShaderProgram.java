package ledkis.module.picturecomparator.programs;

import android.content.Context;

import ledkis.module.picturecomparator.util.ShaderHelper;
import ledkis.module.picturecomparator.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

abstract class ShaderProgram {
    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_COLOR = "u_Color";
    protected static final String U_ALPHA = "u_Alpha";
    protected static final String U_BRIGHTNESS = "u_Brightness";
    protected static final String U_CONTRAST = "u_Contrast";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";    
    protected static final String A_COLOR = "a_Color";    
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    // Shader program
    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId,
        int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
            TextResourceReader
                .readTextFileFromResource(context, vertexShaderResourceId),
            TextResourceReader
                .readTextFileFromResource(context, fragmentShaderResourceId));
    }

    protected ShaderProgram(Context context, String vertexShaderResource,
                            String fragmentShaderResource) {
        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(vertexShaderResource, fragmentShaderResource);
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }
}
