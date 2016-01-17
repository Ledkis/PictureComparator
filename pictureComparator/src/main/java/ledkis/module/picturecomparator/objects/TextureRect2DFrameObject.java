package ledkis.module.picturecomparator.objects;

import ledkis.module.picturecomparator.data.VertexArray;
import ledkis.module.picturecomparator.programs.TextureShaderProgram;
import ledkis.module.picturecomparator.util.Utils;

import static ledkis.module.picturecomparator.Constants.BYTES_PER_FLOAT;

public class TextureRect2DFrameObject extends Rect2DFrame {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;


    private VertexArray textureVertexArray;


    public TextureRect2DFrameObject(float width, float height, float clipWidthFactor, float
            clipHeightFactor) {
        super(width, height);

        clipTexture(clipWidthFactor, clipHeightFactor);
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

    public void clipTexture(float clipWidthFactor, float clipHeightFactor) {
        // Have to be between 0.5 and 1 to avoid inversion
        clipWidthFactor = Utils.map(clipWidthFactor, 0f, 1f, 0.5f, 1f);
        clipHeightFactor = Utils.map(clipHeightFactor, 0f, 1f, 0.5f, 1f);

        final float[] TEXTURE_DATA = {
                // Order of coordinates: S, T

                // Triangle Fan
                0.5f, 0.5f,
                1f - clipWidthFactor, clipHeightFactor,
                clipWidthFactor, clipHeightFactor,
                clipWidthFactor, 1f - clipHeightFactor,
                1f - clipWidthFactor, 1f - clipHeightFactor,
                1f - clipWidthFactor, clipHeightFactor};


        textureVertexArray = new VertexArray(TEXTURE_DATA);
    }

}
