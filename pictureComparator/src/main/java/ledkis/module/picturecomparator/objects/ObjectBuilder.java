package ledkis.module.picturecomparator.objects;

import java.util.ArrayList;
import java.util.List;

import ledkis.module.picturecomparator.util.Geometry2D.Rect2D;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

class ObjectBuilder {
    private static final int FLOATS_PER_VERTEX = 3;

    static interface DrawCommand {
        void draw();
    }

    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }


    static GeneratedData createRect2DFrame(Rect2D rect2D) {
        ObjectBuilder builder = new ObjectBuilder(6);
        builder.appendRect2D(rect2D);
        return builder.build();
    }


    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    private final float[] vertexData;
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();
    private int offset = 0;

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    private void appendRect2D(Rect2D rect2D) {

        // Center point of fan
        vertexData[offset++] = rect2D.center.x;
        vertexData[offset++] = rect2D.center.y;

        // 4.2     Introducing Triangle Fans

        // position 2
        vertexData[offset++] = -rect2D.width / 2;
        vertexData[offset++] = -rect2D.height / 2;

        // position 3
        vertexData[offset++] = rect2D.width / 2;
        vertexData[offset++] = -rect2D.height / 2;

        // position 4
        vertexData[offset++] = rect2D.width / 2;
        vertexData[offset++] = rect2D.height / 2;

        // position 5
        vertexData[offset++] = -rect2D.width / 2;
        vertexData[offset++] = rect2D.height / 2;

        // position 6
        vertexData[offset++] = -rect2D.width / 2;
        vertexData[offset++] = -rect2D.height / 2;

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
            }
        });
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }
}
