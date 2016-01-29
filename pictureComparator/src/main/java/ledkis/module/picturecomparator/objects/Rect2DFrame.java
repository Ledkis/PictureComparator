package ledkis.module.picturecomparator.objects;

import java.util.List;

import ledkis.module.picturecomparator.data.VertexArray;
import ledkis.module.picturecomparator.objects.ObjectBuilder.DrawCommand;
import ledkis.module.picturecomparator.objects.ObjectBuilder.GeneratedData;
import ledkis.module.picturecomparator.programs.ColorShaderProgram;
import ledkis.module.picturecomparator.util.Geometry2D;
import ledkis.module.picturecomparator.util.Geometry2D.Rect2D;

public class Rect2DFrame {
    private static final int POSITION_COMPONENT_COUNT = 2;

    public final float width, height;

    protected final VertexArray vertexArray;
    protected final List<DrawCommand> drawList;

    public Rect2DFrame(float width, float height) {
        this.width = width;
        this.height = height;

        GeneratedData generatedData = ObjectBuilder.createRect2DFrame(getRect2D());

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public Rect2D getRect2D() {
        return new Rect2D(Geometry2D.CENTER_POINT_2D, width, height);
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

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}