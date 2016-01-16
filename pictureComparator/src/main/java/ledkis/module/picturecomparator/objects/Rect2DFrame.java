package ledkis.module.picturecomparator.objects;

import java.util.List;

import ledkis.module.picturecomparator.data.VertexArray;
import ledkis.module.picturecomparator.objects.ObjectBuilder.DrawCommand;
import ledkis.module.picturecomparator.objects.ObjectBuilder.GeneratedData;
import ledkis.module.picturecomparator.programs.ColorShaderProgram;
import ledkis.module.picturecomparator.util.Geometry;

public class Rect2DFrame {
    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float width, height;

    private static final float[] TEXTURE_DATA = {
            // Order of coordinates: S, T

            // Triangle Fan
            0.5f, 0.5f,
            0f, 0.9f,
            1f, 0.9f,
            1f, 0.1f,
            0f, 0.1f,
            0f, 0.9f};

    protected final VertexArray vertexArray;
    protected final List<DrawCommand> drawList;

    public Rect2DFrame(float width, float height) {
        GeneratedData generatedData = ObjectBuilder.createRectFrame(new Geometry.Rect(Geometry
                .CENTER_POINT, width, height));

        this.width = width;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);


    }

    public void draw() {
        for (DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}