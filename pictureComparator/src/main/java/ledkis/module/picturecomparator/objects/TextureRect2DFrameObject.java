package ledkis.module.picturecomparator.objects;

import ledkis.module.picturecomparator.data.VertexArray;
import ledkis.module.picturecomparator.programs.TextureShaderProgram;

import static ledkis.module.picturecomparator.Constants.BYTES_PER_FLOAT;

public class TextureRect2DFrameObject extends Rect2DFrame {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;


    private static final float[] TEXTURE_DATA = {
            // Order of coordinates: S, T

            // Triangle Fan
            0.5f, 0.5f,
            0f, 0.9f,
            1f, 0.9f,
            1f, 0.1f,
            0f, 0.1f,
            0f, 0.9f};

    private final VertexArray textureVertexArray;


    public TextureRect2DFrameObject(float width, float height) {
        super(width, height);

        textureVertexArray = new VertexArray(TEXTURE_DATA);
    }

    public void bindData(TextureShaderProgram textureProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0);


        textureVertexArray.setVertexAttribPointer(
                0,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                0);
    }

}
